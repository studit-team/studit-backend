package com.studit.domain.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studit.domain.user.dto.LoginRequestDto;
import com.studit.domain.user.dto.LoginResponseDto;
import com.studit.domain.user.dto.UserLgnHstryDto;
import com.studit.domain.user.mapper.UserMapper;
import com.studit.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.objectMapper = new ObjectMapper();

        // 로그인 URL 설정
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            // JSON 요청 본문에서 로그인 정보 추출
            LoginRequestDto loginRequest = objectMapper.readValue(
                    request.getInputStream(),
                    LoginRequestDto.class
            );

            // 인증 토큰 생성 (email을 username으로 사용)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );

            // AuthenticationManager에게 인증 위임
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getAuthorCode(),
                userDetails.getName()

        );

        // 로그인 실패 횟수 초기화
        userMapper.resetLoginFailCount(userDetails.getUserId());

        // 로그인 성공 이력 저장
        saveLoginHistory(userDetails.getUserId(), request, "SUCCESS");

        // 응답 생성
        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .token(token)
                .userId(userDetails.getUserId())
                .username(userDetails.getUser().getUsername())
                .name(userDetails.getUser().getName())
                .authorCode(userDetails.getAuthorCode())
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"로그인 실패\",\"message\":\"이메일 또는 비밀번호가 올바르지 않습니다.\"}");
    }

    private void saveLoginHistory(String userId, HttpServletRequest request, String status) {
        UserLgnHstryDto history = UserLgnHstryDto.builder()
                .userId(userId)
                .lgnDate(LocalDateTime.now())
                .lgnIp(getClientIp(request))
                .lgnSttus(status)
                .build();

        userMapper.insertLoginHistory(history);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
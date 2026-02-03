package com.studit.domain.user.service;


import com.studit.domain.user.dto.SignupRequestDto;
import com.studit.domain.user.dto.UserDTO;
import com.studit.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 중복 확인
     */
    public boolean existsByUsername(String username) {
        UserDTO user = userMapper.findByUsername(username);
        return user != null;
    }

    /**
     * 보안 설정 ID 생성: USRCNFRM_yyyyMMdd001 형식
     */
    private String generateSecurityId() {
        // 현재 날짜를 yyyyMMdd 형식으로 변환
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 오늘 날짜로 시작하는 보안 설정 ID 개수 조회
        String prefix = "USRCNFRM_" + dateStr;
        int count = userMapper.countSecurityIdByPrefix(prefix);
        
        // 시퀀스 번호 생성 (001, 002, 003...)
        String sequence = String.format("%03d", count + 1);
        
        return prefix + sequence;
    }

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequestDto signupRequest) {
        // 1. 보안 설정 ID 생성 (USRCNFRM_yyyyMMdd001 형식)
        String securityId = generateSecurityId();

        // 2. 보안 설정 삽입 (기본 권한: ROLE_USER)
        userMapper.insertUserScrtyEstbs(securityId, "USR01", "ROLE_USER");

        // 3. 사용자 정보 생성
        UserDTO userInfo = UserDTO.builder()
                .userId(UUID.randomUUID().toString())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .phone(signupRequest.getPhone())
                .userStatusCode("Y")
                .sbscrbBe(LocalDateTime.now())
                .lgnAprvYn("Y")
                .lgnFailNocs(0)
                .sctryDtrmnTrgetId(securityId)
                .name(signupRequest.getName())
                .build();

        // 4. 사용자 정보 삽입
        userMapper.insertUserInfo(userInfo);
    }

    /**
     * 비밀번호 암호화 (개발용)
     */
    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
}
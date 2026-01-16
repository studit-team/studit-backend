package com.studit.domain.user.service;

import com.studit.domain.user.dto.UserDTO;
import com.studit.domain.user.entity.User;
import com.studit.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signup(UserDTO dto) {

        //보안설성 Id
        String sctryId = "USRCNFRM_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%03d", (int)(Math.random() * 1000));

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .userStatusCode("Y")
                .sbscrbBe(LocalDateTime.now())
                .lgnAprYn("Y")
                .lgnFailNocs(0)
                .sctryDtrmnTrgetId(sctryId)
                .build();

        userMapper.save(user);
    }


}

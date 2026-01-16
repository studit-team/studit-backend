package com.studit.domain.user.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String userId;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String userStatusCode;
    private LocalDateTime sbscrbBe;
    private String lgnAprYn;
    private Integer lgnFailNocs;
    private String sctryDtrmnTrgetId;

}

package com.studit.domain.user.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String userId;
    private String username;
    private String password;
    private String phone;
    private String userStatusCode;
    private LocalDateTime sbscrbBe;
    private String lgnAprvYn;
    private Integer lgnFailNocs;
    private String sctryDtrmnTrgetId;
    private String name;

    // 권한 정보 (Join)
    private String authorCode;
    private String userTyCode;
}




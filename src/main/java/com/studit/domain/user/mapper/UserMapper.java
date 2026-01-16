package com.studit.domain.user.mapper;

import com.studit.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //회원가입
    void save(User user);

    //이메일 인증
    //int existsByEmail(String email);

    //로그인시 정보 조회
    //User findByUserName(String username);
}

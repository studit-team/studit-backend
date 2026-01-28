package com.studit.domain.user.mapper;

import com.studit.domain.user.dto.UserDTO;
import com.studit.domain.user.dto.UserLgnHstryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 이메일로 사용자 정보 조회 (권한 정보 포함)
     */
    UserDTO findByEmail(@Param("username") String username);

    /**
     * 사용자 ID로 조회
     */
    UserDTO findByUserId(@Param("userId") String userId);

    /**
     * 로그인 실패 횟수 증가
     */
    void incrementLoginFailCount(@Param("userId") String userId);

    /**
     * 로그인 실패 횟수 초기화
     */
    void resetLoginFailCount(@Param("userId") String userId);

    /**
     * 로그인 이력 저장
     */
    void insertLoginHistory(UserLgnHstryDto loginHistory);

    /**
     * 보안 설정 삽입
     */
    void insertUserScrtyEstbs(@Param("securityId") String securityId,
                              @Param("userTyCode") String userTyCode,
                              @Param("authorCode") String authorCode);

    /**
     * 사용자 정보 삽입
     */
    void insertUserInfo(UserDTO userInfo);

    /**
     * 보안 설정 ID 개수 조회 (prefix로 시작하는)
     */
    int countSecurityIdByPrefix(@Param("prefix") String prefix);
}
package com.studit.domain.study.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListRespDto {
    private int boardId;
    private String userId;
    private String username;
    private String title;
    private String boardTyCd;
    private LocalDateTime createdAt;
}

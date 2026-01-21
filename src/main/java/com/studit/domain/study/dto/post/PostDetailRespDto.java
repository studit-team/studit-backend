package com.studit.domain.study.dto.post;

import lombok.Data;

@Data
public class PostDetailRespDto {
    private int boardId;
    private String userId;
    private String username;
    private String title;
    private String content;
    private String fileUrl;
    private String boardTyCd;
}

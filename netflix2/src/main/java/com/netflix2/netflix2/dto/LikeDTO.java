package com.netflix2.netflix2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeDTO {
    private String movieId;
    private Long likeCount;
}

package com.camel.clinic.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReviewDto {
    private Integer rating;

    private String title;

    private String content;
}

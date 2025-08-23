package com.pawtner.WithPet.domain.noseprint.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoseMatchResultResponse {

    private String nosePrintImg;
    private String searchDatetime;
    private String searchType;
    private Double highest_score;
    private String hightest_nose_print_img;
    private Long hightest_post_id;

}
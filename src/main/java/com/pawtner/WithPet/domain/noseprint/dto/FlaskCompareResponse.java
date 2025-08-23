package com.pawtner.WithPet.domain.noseprint.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
public class FlaskCompareResponse extends ArrayList<FlaskCompareResponse.FlaskResult> {

    @Getter
    @NoArgsConstructor
    public static class FlaskResult {
        private String nose_print_img;
        private Long post_id;
        private Double similarity;
    }
}
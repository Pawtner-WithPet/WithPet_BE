package com.pawtner.WithPet.domain.walk.dto;

import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostLocationPostResponse {
    private Long post_id;
    private Long pet_id;
    private String dog_nm;
    private String kind_nm;

    private LostWalkPath lostWalkPath;
}

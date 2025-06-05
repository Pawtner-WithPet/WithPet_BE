package com.pawtner.WithPet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
public class PetUpdateRequest {
    private Long userId;
    private String features;
}

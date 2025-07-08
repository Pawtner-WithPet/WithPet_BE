package com.pawtner.WithPet.domain.noseprint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NosePrintResultResponse {
    private Long petId;
    private Long resultId;
    private Long ownerId;
    private Long nosePrintId;
    private String nosePrintImg;
    private Float matchRate;
    private Boolean isMyMissingPet;
}

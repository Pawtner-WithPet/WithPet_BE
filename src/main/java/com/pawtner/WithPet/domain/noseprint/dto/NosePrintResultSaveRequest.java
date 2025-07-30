package com.pawtner.WithPet.domain.noseprint.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class NosePrintResultSaveRequest {

    private Long searchId;
    private Long ownerId;
    private Long nosePrintId;
    private Float matchRate;
    private Boolean isMyMissingPet;

}

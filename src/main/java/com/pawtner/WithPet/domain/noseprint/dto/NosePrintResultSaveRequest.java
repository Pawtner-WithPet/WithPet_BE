package com.pawtner.WithPet.domain.noseprint.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class NosePrintResultSaveRequest {

    // 일단 이름만 변경해둠 나중에 수정해야함
    private Long resultId;
    private Long ownerId;
    private Long nosePrintId;
    private Float matchRate;

}

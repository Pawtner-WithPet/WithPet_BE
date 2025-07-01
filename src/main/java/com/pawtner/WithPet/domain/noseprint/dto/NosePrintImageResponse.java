package com.pawtner.WithPet.domain.noseprint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NosePrintImageResponse {

    private Long nosePrintId;
    private String nosePrintImg; // S3 URL
    private Long petId; // 연결된 Pet ID (nullable)
    private Long ownerId; // 연결된 Member ID (nullable)
    private LocalDateTime registerDatetime;

}

package com.pawtner.WithPet.domain.noseprint.dto;

import com.pawtner.WithPet.domain.noseprint.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NosePrintListDetailResponse {
    private Long searchId;
    private Long ownerId;
    private String nosePrintImg;
    private String searchLocation;
    private LocalDateTime searchDatetime;
    private SearchType searchType;
    private List<NosePrintResultResponse> result;
}

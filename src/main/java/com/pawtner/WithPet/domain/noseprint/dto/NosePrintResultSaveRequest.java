package com.pawtner.WithPet.domain.noseprint.dto;

import com.pawtner.WithPet.domain.noseprint.enums.SearchType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NosePrintResultSaveRequest {
    private String nosePrintImg;
    private LocalDateTime searchDatetime;
    private SearchType searchType;
    private Float highestScore;
    private String highestNosePrintImg;
    private Long highestPostId;
    private Long ownerId;
    private String searchLocation;

     private List<CandidateSave> candidates;


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CandidateSave {
        private Long ownerId;
        private Long nosePrintId;
        private Float matchRate;
        private String nosePrintImg;
        private Long postId;
    }
}
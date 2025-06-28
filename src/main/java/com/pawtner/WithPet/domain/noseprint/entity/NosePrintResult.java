package com.pawtner.WithPet.domain.noseprint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "noseprint_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NosePrintResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "search_id", nullable = false)
    private Long searchId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "nose_print_id", nullable = false)
    private Long nosePrintId;

    @Column(name = "match_rate", nullable = false)
    private Float matchRate;

    @Column(name = "is_my_missing_pet", nullable = false)
    private Boolean isMyMissingPet;

    @Builder
    public NosePrintResult(Long searchId, Long ownerId, Long nosePrintId,
                           Float matchRate, Boolean isMyMissingPet) {
        this.searchId = searchId;
        this.ownerId = ownerId;
        this.nosePrintId = nosePrintId;
        this.matchRate = matchRate;
        this.isMyMissingPet = isMyMissingPet;
    }
}

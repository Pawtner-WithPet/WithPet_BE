package com.pawtner.WithPet.domain.noseprint.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_id", nullable = false)
    private NosePrintSearch search;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nose_print_id", nullable = false)
    private NosePrint nosePrint;

    @Column(name = "match_rate", nullable = false)
    private Float matchRate;

    @Column(name = "is_my_missing_pet", nullable = false)
    private Boolean isMyMissingPet;

    @Builder
    public NosePrintResult(NosePrintSearch search, Member owner, NosePrint nosePrint,
                           Float matchRate, Boolean isMyMissingPet) {
        this.search = search;
        this.owner = owner;
        this.nosePrint = nosePrint;
        this.matchRate = matchRate;
        this.isMyMissingPet = isMyMissingPet;
    }
}

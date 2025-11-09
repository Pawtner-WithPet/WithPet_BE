package com.pawtner.WithPet.domain.noseprint.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nose_match_candidate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoseMatchCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long candidateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private NoseMatchResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nose_print_id", nullable = false)
    private NosePrint nosePrint;

    @Column(name = "match_rate", nullable = false)
    private Float matchRate;

    @Column(name = "nose_print_img", nullable = false, length = 200)
    private String nosePrintImg;

    @Column(name = "post_id", nullable = false)
    private Long postId;


    public void setResult(NoseMatchResult result) { this.result = result; }


    @Builder
    public NoseMatchCandidate(NoseMatchResult result, Member owner, NosePrint nosePrint,
                              String nosePrintImg, Float matchRate, Long postId) {
        this.result = result;
        this.owner = owner;
        this.nosePrint = nosePrint;
        this.matchRate = matchRate;
        this.nosePrintImg = nosePrintImg;
        this.postId = postId;
    }

}
package com.pawtner.WithPet.domain.noseprint.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.noseprint.enums.SearchType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "nose_match_result")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NoseMatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(name = "nose_print_img", nullable = false, length = 200)
    private String nosePrintImg;

    @Column(name = "search_location", nullable = false, length = 100)
    private String searchLocation;

//    @CreatedDate
    @Column(name = "search_datetime", nullable = false)
    private LocalDateTime searchDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_type", columnDefinition = "ENUM('LOST','FOUND')", nullable = false)
    private SearchType searchType;

    @Column(name = "highest_score", nullable = false)
    private Float highestScore;

    @Column(name = "highest_nose_print_img", nullable = false)
    private String highestNosePrintImg;

    @Column(name = "highest_post_id", nullable = false)
    private Long highestPostId;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    @Builder.Default
    private List<NoseMatchCandidate> results = new ArrayList<>();

    public void addCandidate(NoseMatchCandidate c) {
        results.add(c);
        c.setResult(this);
    }
 }
package com.pawtner.WithPet.domain.noseprint.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.noseprint.enums.SearchType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NosePrintSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(nullable = false)
    private String nosePrintImg;

    @Column(nullable = false)
    private String searchLocation;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime searchDatetime;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('LOST','FOUND')", nullable = false)
    private SearchType searchType;

    @Column(nullable = false)
    private Float highestScore;

    @OneToMany(mappedBy = "search", cascade = CascadeType.ALL)
    private List<NosePrintResult> results = new ArrayList<>();
}

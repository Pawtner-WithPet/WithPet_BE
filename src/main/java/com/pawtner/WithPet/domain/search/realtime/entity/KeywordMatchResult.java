package com.pawtner.WithPet.domain.search.realtime.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordMatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="job_id")
    private RealTimeSearch job;

    @Column(nullable=false)
    private String sourceType;  // FOUND(발견 동물) or SHELTER(보호소) or SNS

    @Column(nullable=false)
    private String refKey;      // 중복 방지

    private String title;

    private String imageUrl;

    private String location;

    @Lob
    private String text;

    private LocalDateTime eventDate;

    private String matchedKeywords;

    private Integer matchedCount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

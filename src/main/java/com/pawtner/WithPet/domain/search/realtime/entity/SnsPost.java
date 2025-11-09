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
public class SnsPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String platform;

    @Column(nullable=false)
    private String externalId;

    private String postUrl;

    private String imageUrl;

    @Lob
    private String text;

    private String region;

    private LocalDateTime postedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

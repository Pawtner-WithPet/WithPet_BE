package com.pawtner.WithPet.domain.walk.service;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.repository.PetRepository;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.exception.LostPetPostNotFoundException;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import com.pawtner.WithPet.domain.walk.dto.*;
import com.pawtner.WithPet.domain.walk.entity.Walk;
import com.pawtner.WithPet.domain.walk.entity.WalkPet;
import com.pawtner.WithPet.domain.walk.entity.WalkPhoto;
import com.pawtner.WithPet.domain.walk.entity.WalkRoutePoint;
import com.pawtner.WithPet.domain.walk.repository.WalkRepository;
import com.pawtner.WithPet.domain.walk.repository.WalkRoutePointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalkService {

    private final LostPetPostRepository lostPetPostRepository;
    private final WalkRepository walkRepository;
    private final WalkRoutePointRepository routePointRepository;
    private final PetRepository petRepository;

    // 실종견 위치 조회
    public List<LostLocationResponse> getLostLocation(String latitude, String longitude) {

        List<LostPetPost> lostList = lostPetPostRepository.findAll();

        double lat = Double.parseDouble(latitude);
        double lng = Double.parseDouble(longitude);
        double radiusKm = 2.0;

        // 거리 확인 - 일단 임의로 반경 2km로 거리 제한
        return lostList.stream()
                .filter(r -> calculateDistance(lat, lng, r.getLostLocation_latitude(), r.getLostLocation_longitude()) <= radiusKm)
                .map(LostLocationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, String lostLocation_latitude, String lostLocation_longitude) {
        final int EARTH_RADIUS = 6371;
        double lat2 = Double.parseDouble(lostLocation_latitude);
        double lng2 = Double.parseDouble(lostLocation_longitude);

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    // 실종견 게시글 이동
    public LostLocationPostResponse getLostWalk(Long postId) {
        // 실종견 게시글 정보 가져오기
        LostPetPost post = lostPetPostRepository.findWithAllByLostPostId(postId)
                .orElseThrow(() -> new LostPetPostNotFoundException("해당 실종글을 찾을 수 없습니다. postId=" + postId));

        // 실종견 이름, 견종 가져오기
        Pet pet = post.getPet();
        Long petId = pet.getId();

        List<Walk> selectedWalks = walkRepository.findByWalkPets_Pet_IdAndIsSelectedTrueOrderByEndedAtDesc(petId);

        LostWalkPath lostWalkPath = null;

        if (selectedWalks != null && !selectedWalks.isEmpty()) {
            Walk selected = selectedWalks.get(0);

            List<WalkRoutePoint> routeEntities = routePointRepository.findByWalk_WalkIdOrderBySeqAsc(selected.getWalkId());

            List<RoutePointDto> routeDtos = routeEntities.stream()
                    .map(r -> RoutePointDto.builder()
                            .seq(r.getSeq())
                            .lat(r.getLat())
                            .lng(r.getLng())
                            .build())
                    .collect(Collectors.toList());

            lostWalkPath = LostWalkPath.builder()
                    .walkId(selected.getWalkId())
                    .startedAt(selected.getStartedAt())
                    .endedAt(selected.getEndedAt())
                    .route(routeDtos)
                    .build();
        }

        LostLocationPostResponse res = new LostLocationPostResponse();
        res.setPost_id(post.getLostPostId());
        res.setPet_id(pet.getId());
        res.setDog_nm(pet.getDogNm());
        res.setKind_nm(pet.getKindNm());
        res.setLostWalkPath(lostWalkPath);

        return res;
    }

    // 산책 기록 저장
    @Transactional
    public Long saveWalk(WalkRequestDto request) {
        Walk walk = Walk.builder()
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .durationSec(request.getDurationSec())
                .distanceM(request.getDistanceM())
                .avgSpeed(request.getAvgSpeed() != null ? BigDecimal.valueOf(request.getAvgSpeed()) : null)
                .ownerId(request.getOwnerId())
                .isSelected(Boolean.TRUE.equals(request.getIsSelected()))
                .build();

        // 산책 경로 설정
        if (request.getRoute() != null && !request.getRoute().isEmpty()) {
            List<WalkRoutePoint> routePoints = request.getRoute().stream().map(r -> {
                WalkRoutePoint rp = WalkRoutePoint.builder()
                        .walk(walk)
                        .seq(r.getSeq())
                        .lat(r.getLat())
                        .lng(r.getLng())
                        .build();
                return rp;
            }).collect(Collectors.toList());
            walk.setRoutePoints(routePoints);
        }

        // 산책견 설정
        if (request.getPetIds() != null && !request.getPetIds().isEmpty()) {
            List<WalkPet> walkPets = new ArrayList<>();
            for (Long petId : request.getPetIds()) {
                Pet pet = petRepository.findById(petId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 petId: " + petId));
                WalkPet wp = WalkPet.builder()
                        .walk(walk)
                        .pet(pet)
                        .build();
                walkPets.add(wp);
            }
            walk.setWalkPets(walkPets);
        }

        // 사진
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            List<WalkPhoto> photos = request.getPhotos().stream().map(p -> {
                WalkPhoto wp = WalkPhoto.builder()
                        .walk(walk)
                        .walkImg(p.getUrl())
                        .takenAt(p.getTakenAt() != null ? p.getTakenAt() : LocalDateTime.now())
                        .build();
                if (p.getLat() != null) wp.setLat(BigDecimal.valueOf(p.getLat()));
                if (p.getLng() != null) wp.setLng(BigDecimal.valueOf(p.getLng()));
                return wp;
            }).collect(Collectors.toList());
            walk.setPhotos(photos);
        }

        Walk saved = walkRepository.save(walk);
        return saved.getWalkId();
    }
}

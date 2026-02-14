package com.aiuservices.aiservice.service;


import com.aiuservices.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;


    public Object getUserRecommendations(String userId) {

        return recommendationRepository.findByUserId(userId);
    }

    public Object getActivityRecommendations(String activityId) {

        return  recommendationRepository.findByActivityId(activityId)
                .orElseThrow(()-> new RuntimeException("No recommendations found for activityId: " + activityId));
    }
}

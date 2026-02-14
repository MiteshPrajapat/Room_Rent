package com.aiuservices.aiservice.service;

import com.aiuservices.aiservice.model.Activity;
import com.aiuservices.aiservice.model.Recommendation;
import com.aiuservices.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ACtivityMessageListener {
    private final ActivityAiService activityAiService;
    private  final RecommendationRepository recommendationRepository;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    public void processActivity(Activity activity) {
        log.info("Received activity message: {}", activity.getUserId());
        // Here you can add logic to process the activity, e.g., save to database, trigger other services, etc.
        Recommendation recommendation = activityAiService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }


}

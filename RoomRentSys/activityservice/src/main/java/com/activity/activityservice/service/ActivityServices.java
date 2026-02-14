package com.activity.activityservice.service;


import com.activity.activityservice.dto.ActivityRequest;
import com.activity.activityservice.dto.ActivityResponse;
import com.activity.activityservice.models.Activity;
import com.activity.activityservice.repository.ActivityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityServices {
    private final  ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;
    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if (!isValidUser) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionMetics(request.getAdditionMetics())
                .build();
        Activity savedAct = activityRepository.save(activity);
        try {
            kafkaTemplate.send(topicName,savedAct.getUserId(),savedAct);
        }catch (Exception e) {
            // Handle Kafka exceptions
           e.printStackTrace();
        }

        return mapToResponse(savedAct);



    }

    private ActivityResponse mapToResponse(Activity savedAct) {
        ActivityResponse response = new ActivityResponse();
        response.setId(savedAct.getId());
        response.setUserId(savedAct.getUserId());
        response.setType(savedAct.getType());
        response.setDuration(savedAct.getDuration());
        response.setCaloriesBurned(savedAct.getCaloriesBurned());
        response.setStartTime(savedAct.getStartTime());
        response.setAdditionMetics(savedAct.getAdditionMetics());
        response.setCreatedAt(savedAct.getCreatedAt());
        response.setUpdatedAt(savedAct.getUpdatedAt());

        return response;
    }
}

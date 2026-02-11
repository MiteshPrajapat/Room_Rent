package com.activity.activityservice.controller;


import com.activity.activityservice.dto.ActivityRequest;
import com.activity.activityservice.dto.ActivityResponse;
import com.activity.activityservice.models.Activity;
import com.activity.activityservice.service.ActivityServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {
    private ActivityServices activityServices;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityServices.trackActivity(request));
    }
}

package com.activity.activityservice.repository;

import com.activity.activityservice.models.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
}

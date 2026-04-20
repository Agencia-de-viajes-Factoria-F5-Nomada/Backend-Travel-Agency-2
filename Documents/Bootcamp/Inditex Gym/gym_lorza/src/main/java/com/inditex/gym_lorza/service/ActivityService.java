package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.exception.ActivityNotFoundException;
import com.inditex.gym_lorza.model.Activity;
import com.inditex.gym_lorza.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getAll() {
        return activityRepository.findAll();
    }

    public Optional<Activity> findActivity(Long id) {
        return activityRepository.findById(id);
    }

    public Activity addActivity(Activity newActivity) {
        return activityRepository.save(newActivity);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public Activity updateActivity(Long id, Activity updatedActivity) {
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));

        return existingActivity;
    }  }

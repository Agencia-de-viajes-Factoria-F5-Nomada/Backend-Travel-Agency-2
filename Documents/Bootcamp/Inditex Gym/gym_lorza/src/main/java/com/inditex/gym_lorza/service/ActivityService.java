package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.exception.ObjectNotFoundException;
import com.inditex.gym_lorza.model.Activity;
import com.inditex.gym_lorza.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<Activity> getAll() {
        return activityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Activity> findActivity(Long id) {
        return activityRepository.findById(id);
    }

    @Transactional
    public Activity addActivity(Activity newActivity) {
        return activityRepository.save(newActivity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    @Transactional
    public Activity updateActivity(Long id, Activity updatedActivity) {
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("actividad", id));
        existingActivity.setTitle(updatedActivity.getTitle());
        existingActivity.setDescription(updatedActivity.getDescription());
        existingActivity.setPrice(updatedActivity.getPrice());
        existingActivity.setDate(updatedActivity.getDate());
        existingActivity.setStartHour(updatedActivity.getStartHour());
        existingActivity.setEndHour(updatedActivity.getEndHour());
        return activityRepository.save(existingActivity);
    }
}
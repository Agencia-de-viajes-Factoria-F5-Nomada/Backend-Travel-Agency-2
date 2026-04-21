package com.inditex.gym_lorza.mapper;

import com.inditex.gym_lorza.dto.ActivityRequestDTO;
import com.inditex.gym_lorza.dto.ActivityResponseDTO;
import com.inditex.gym_lorza.model.Activity;

public class ActivityMapper {

    public static Activity toEntity(ActivityRequestDTO dto) {
        Activity activity = new Activity();
        activity.setTitle(dto.getTitle());
        activity.setPrice(dto.getPrice());
        activity.setWeekDay(dto.getWeekDay());
        activity.setStartHour(dto.getStartHour());
        activity.setEndHour(dto.getEndHour());
        activity.setImage(dto.getImage());
        return activity;
    }

    public static ActivityResponseDTO toDTO(Activity activity) {
        ActivityResponseDTO dto = new ActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setPrice(activity.getPrice());
        dto.setWeekDay(activity.getWeekDay());
        dto.setStartHour(activity.getStartHour());
        dto.setEndHour(activity.getEndHour());
        dto.setImage(activity.getImage());
        if (activity.getTrainer() != null) {
            dto.setTrainerName(activity.getTrainer().getName());
        }
        return dto;
    }
}
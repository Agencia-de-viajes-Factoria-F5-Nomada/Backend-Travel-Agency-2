package com.inditex.gym_lorza.mapper;

import com.inditex.gym_lorza.dto.ActivityRequestDTO;
import com.inditex.gym_lorza.dto.ActivityResponseDTO;
import com.inditex.gym_lorza.model.Activity;
import com.inditex.gym_lorza.model.Trainer;

public class ActivityMapper {

    public static Activity toEntity(ActivityRequestDTO dto, Trainer trainer) {
        Activity activity = new Activity();
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setPrice(dto.getPrice());
        activity.setDate(dto.getDate());
        activity.setStartHour(dto.getStartHour());
        activity.setEndHour(dto.getEndHour());
        activity.setImage(dto.getImage());
        activity.setTrainer(trainer);
        return activity;
    }

    public static ActivityResponseDTO toDTO(Activity activity) {
        ActivityResponseDTO dto = new ActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setPrice(activity.getPrice());
        dto.setDate(activity.getDate());
        dto.setStartHour(activity.getStartHour());
        dto.setEndHour(activity.getEndHour());
        dto.setImage(activity.getImage());
        if (activity.getTrainer() != null) {
            dto.setTrainerName(activity.getTrainer().getName());
        }
        return dto;
    }
}
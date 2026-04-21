package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.dto.ActivityRequestDTO;
import com.inditex.gym_lorza.dto.ActivityResponseDTO;
import com.inditex.gym_lorza.exception.ObjectNotFoundException;
import com.inditex.gym_lorza.mapper.ActivityMapper;
import com.inditex.gym_lorza.model.Activity;
import com.inditex.gym_lorza.model.Trainer;
import com.inditex.gym_lorza.repository.ActivityRepository;
import com.inditex.gym_lorza.repository.TrainerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TrainerRepository trainerRepository;

    public ActivityService(ActivityRepository activityRepository, TrainerRepository trainerRepository) {
        this.activityRepository = activityRepository;
        this.trainerRepository = trainerRepository;
    }

    public List<ActivityResponseDTO> getAll() {
        return activityRepository.findAll()
                .stream()
                .map(ActivityMapper::toDTO)
                .toList();
    }

    public ActivityResponseDTO findActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("actividad", id));
        return ActivityMapper.toDTO(activity);
    }

    public ActivityResponseDTO addActivity(ActivityRequestDTO dto) {
        Activity activity = ActivityMapper.toEntity(dto);
        if (dto.getTrainerId() != null) {
            Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                    .orElseThrow(() -> new ObjectNotFoundException("entrenadora", dto.getTrainerId()));
            activity.setTrainer(trainer);
        }
        return ActivityMapper.toDTO(activityRepository.save(activity));
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public ActivityResponseDTO updateActivity(Long id, ActivityRequestDTO dto) {
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("actividad", id));

        existingActivity.setTitle(dto.getTitle());
        existingActivity.setPrice(dto.getPrice());
        existingActivity.setWeekDay(dto.getWeekDay());
        existingActivity.setStartHour(dto.getStartHour());
        existingActivity.setEndHour(dto.getEndHour());
        existingActivity.setImage(dto.getImage());

        if (dto.getTrainerId() != null) {
            Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                    .orElseThrow(() -> new ObjectNotFoundException("entrenadora", dto.getTrainerId()));
            existingActivity.setTrainer(trainer);
        }

        return ActivityMapper.toDTO(activityRepository.save(existingActivity));
    }
}
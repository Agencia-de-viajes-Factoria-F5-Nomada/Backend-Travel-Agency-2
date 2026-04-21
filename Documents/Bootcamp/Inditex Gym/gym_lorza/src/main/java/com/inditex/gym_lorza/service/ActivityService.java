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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TrainerRepository trainerRepository;

    public ActivityService(ActivityRepository activityRepository, TrainerRepository trainerRepository) {
        this.activityRepository = activityRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getAll() {
        return activityRepository.findAll()
                .stream()
                .map(ActivityMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponseDTO findActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("actividad", id));
        return ActivityMapper.toDTO(activity);
    }

    @Transactional
    public ActivityResponseDTO addActivity(ActivityRequestDTO dto) {
        Trainer trainer = resolveTrainer(dto.getTrainerId());
        Activity activity = ActivityMapper.toEntity(dto, trainer);
        return ActivityMapper.toDTO(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new ObjectNotFoundException("actividad", id);
        }
        activityRepository.deleteById(id);
    }

    @Transactional
    public ActivityResponseDTO updateActivity(Long id, ActivityRequestDTO dto) {
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("actividad", id));
        Trainer trainer = resolveTrainer(dto.getTrainerId());
        existingActivity.setTitle(dto.getTitle());
        existingActivity.setDescription(dto.getDescription());
        existingActivity.setPrice(dto.getPrice());
        existingActivity.setDate(dto.getDate());
        existingActivity.setStartHour(dto.getStartHour());
        existingActivity.setEndHour(dto.getEndHour());
        existingActivity.setTrainer(trainer);
        return ActivityMapper.toDTO(activityRepository.save(existingActivity));
    }

    private Trainer resolveTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ObjectNotFoundException("entrenadora", trainerId));
    }
}
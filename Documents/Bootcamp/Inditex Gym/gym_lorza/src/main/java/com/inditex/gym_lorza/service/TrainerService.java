package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.dto.TrainerRequestDTO;
import com.inditex.gym_lorza.dto.TrainerResponseDTO;
import com.inditex.gym_lorza.exception.ObjectNotFoundException;
import com.inditex.gym_lorza.mapper.TrainerMapper;
import com.inditex.gym_lorza.model.Trainer;
import com.inditex.gym_lorza.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainerResponseDTO> getAll() {
        return trainerRepository.findAll()
                .stream()
                .map(TrainerMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TrainerResponseDTO findTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("entrenadora", id));
        return TrainerMapper.toDTO(trainer);
    }

    @Transactional
    public TrainerResponseDTO addTrainer(TrainerRequestDTO dto) {
        Trainer trainer = TrainerMapper.toEntity(dto);
        return TrainerMapper.toDTO(trainerRepository.save(trainer));
    }

    @Transactional
    public void deleteTrainer(Long id) {
        if (!trainerRepository.existsById(id)) {
            throw new ObjectNotFoundException("entrenadora", id);
        }
        trainerRepository.deleteById(id);
    }

    @Transactional
    public TrainerResponseDTO updateTrainer(Long id, TrainerRequestDTO dto) {
        Trainer existingTrainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("entrenadora", id));
        existingTrainer.setName(dto.getName());
        existingTrainer.setDni(dto.getDni());
        existingTrainer.setHiringYear(dto.getHiringYear());
        existingTrainer.setIsHired(dto.getIsHired());
        existingTrainer.setImage(dto.getImage());
        return TrainerMapper.toDTO(trainerRepository.save(existingTrainer));
    }
}
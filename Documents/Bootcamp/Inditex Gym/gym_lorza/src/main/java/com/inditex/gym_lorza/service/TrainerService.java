package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.exception.ObjectNotFoundException;
import com.inditex.gym_lorza.model.Trainer;
import com.inditex.gym_lorza.repository.TrainerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public List<Trainer> getAll() {
        return trainerRepository.findAll();
    }

    public Optional<Trainer> findTrainer(Long id) {
        return trainerRepository.findById(id);
    }

    public Trainer addTrainer(Trainer newTrainer) {
        return trainerRepository.save(newTrainer);
    }

    public void deleteTrainer(Long id) {
        trainerRepository.deleteById(id);
    }

    public Trainer updateTrainer(Long id, Trainer updatedTrainer) {
        Trainer existingTrainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("entrenadora", id));

        existingTrainer.setName(updatedTrainer.getName());
        existingTrainer.setDni(updatedTrainer.getDni());
        existingTrainer.setHiringYear(updatedTrainer.getHiringYear());
        existingTrainer.setIsHired(updatedTrainer.getIsHired());
        existingTrainer.setImage(updatedTrainer.getImage());

        return trainerRepository.save(existingTrainer);
    }
}
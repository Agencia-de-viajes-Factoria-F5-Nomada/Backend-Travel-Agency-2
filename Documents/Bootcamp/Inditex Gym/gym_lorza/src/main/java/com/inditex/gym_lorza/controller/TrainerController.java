package com.inditex.gym_lorza.controller;

import com.inditex.gym_lorza.dto.ActivityResponseDTO;
import com.inditex.gym_lorza.dto.TrainerRequestDTO;
import com.inditex.gym_lorza.dto.TrainerResponseDTO;
import com.inditex.gym_lorza.service.ActivityService;
import com.inditex.gym_lorza.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/trainers")
@CrossOrigin(origins = "http://localhost:3000")
public class TrainerController {

    private final TrainerService trainerService;
    private final ActivityService activityService;

    public TrainerController(TrainerService trainerService, ActivityService activityService) {
        this.trainerService = trainerService;
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<TrainerResponseDTO> createTrainer(@Valid @RequestBody TrainerRequestDTO dto) {
        TrainerResponseDTO saved = trainerService.addTrainer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<TrainerResponseDTO> getAllTrainers() {
        return trainerService.getAll();
    }

    @GetMapping("/active")
    public List<TrainerResponseDTO> getActiveTrainers() {
        return trainerService.findActiveTrainers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponseDTO> getTrainerById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.findTrainer(id));
    }

    @GetMapping("/{id}/activities")
    public List<ActivityResponseDTO> getTrainerActivities(@PathVariable Long id) {
        return activityService.findByTrainerId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponseDTO> updatedTrainerById(@PathVariable Long id, @Valid @RequestBody TrainerRequestDTO dto) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}
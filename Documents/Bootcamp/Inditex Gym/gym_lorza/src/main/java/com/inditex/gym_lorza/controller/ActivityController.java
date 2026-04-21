package com.inditex.gym_lorza.controller;

import com.inditex.gym_lorza.dto.ActivityRequestDTO;
import com.inditex.gym_lorza.dto.ActivityResponseDTO;
import com.inditex.gym_lorza.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/activities")
@CrossOrigin(origins = "http://localhost:3000")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityRequestDTO dto) {
        ActivityResponseDTO saved = activityService.addActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<ActivityResponseDTO> getAllActivities() {
        return activityService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.findActivity(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivityById(@PathVariable Long id, @Valid @RequestBody ActivityRequestDTO dto) {
        return ResponseEntity.ok(activityService.updateActivity(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
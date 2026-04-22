package com.inditex.gym_lorza.controller;

import com.inditex.gym_lorza.dto.ActivityRequestDTO;
import com.inditex.gym_lorza.dto.ActivityResponseDTO;
import com.inditex.gym_lorza.dto.UserResponseDTO;
import com.inditex.gym_lorza.service.ActivityService;
import com.inditex.gym_lorza.service.EnrollmentService;
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
    private final EnrollmentService enrollmentService;

    public ActivityController(ActivityService activityService, EnrollmentService enrollmentService) {
        this.activityService = activityService;
        this.enrollmentService = enrollmentService;
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

    @GetMapping("/future")
    public List<ActivityResponseDTO> getFutureActivities() {
        return activityService.findFutureActivities();
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

    @PostMapping("/{activityId}/users/{userId}")
    public ResponseEntity<Void> enrollUser(@PathVariable Long activityId, @PathVariable Long userId) {
        enrollmentService.enrollUser(activityId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{activityId}/users/{userId}")
    public ResponseEntity<Void> unenrollUser(@PathVariable Long activityId, @PathVariable Long userId) {
        enrollmentService.unenrollUser(activityId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{activityId}/users")
    public List<UserResponseDTO> getEnrolledUsers(@PathVariable Long activityId) {
        return enrollmentService.getEnrolledUsers(activityId);
    }
}
package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.dto.UserResponseDTO;
import com.inditex.gym_lorza.exception.MaxActivitiesReachedException;
import com.inditex.gym_lorza.exception.ObjectNotFoundException;
import com.inditex.gym_lorza.exception.PaymentRequiredException;
import com.inditex.gym_lorza.exception.UserAlreadyEnrolledException;
import com.inditex.gym_lorza.mapper.UserMapper;
import com.inditex.gym_lorza.model.Activity;
import com.inditex.gym_lorza.model.User;
import com.inditex.gym_lorza.repository.ActivityRepository;
import com.inditex.gym_lorza.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public EnrollmentService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void enrollUser(Long activityId, Long userId) {
        Activity activity = findActivityOrThrow(activityId);
        User user = findUserOrThrow(userId);

        validateAnnualFeePaid(user);
        validateNotAlreadyEnrolled(activity, userId, activityId);
        validateMaxFutureActivities(userId);

        activity.getUsers().add(user);
        activityRepository.save(activity);
    }

    @Transactional
    public void unenrollUser(Long activityId, Long userId) {
        Activity activity = findActivityOrThrow(activityId);
        findUserOrThrow(userId);

        activity.getUsers().removeIf(u -> u.getId().equals(userId));
        activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getEnrolledUsers(Long activityId) {
        Activity activity = findActivityOrThrow(activityId);
        return activity.getUsers().stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    private void validateAnnualFeePaid(User user) {
        if (!user.getAnnualFeePaid()) {
            throw new PaymentRequiredException(user.getId());
        }
    }

    private void validateNotAlreadyEnrolled(Activity activity, Long userId, Long activityId) {
        boolean alreadyEnrolled = activity.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (alreadyEnrolled) {
            throw new UserAlreadyEnrolledException(userId, activityId);
        }
    }

    private void validateMaxFutureActivities(Long userId) {
        long futureCount = activityRepository.countByUsersIdAndDateAfter(userId, LocalDate.now());
        if (futureCount >= 3) {
            throw new MaxActivitiesReachedException(userId);
        }
    }

    private Activity findActivityOrThrow(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ObjectNotFoundException("actividad", activityId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("usuaria", userId));
    }
}
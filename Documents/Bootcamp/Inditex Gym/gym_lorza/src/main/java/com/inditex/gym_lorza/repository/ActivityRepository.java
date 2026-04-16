package com.inditex.gym_lorza.repository;

import com.inditex.gym_lorza.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}

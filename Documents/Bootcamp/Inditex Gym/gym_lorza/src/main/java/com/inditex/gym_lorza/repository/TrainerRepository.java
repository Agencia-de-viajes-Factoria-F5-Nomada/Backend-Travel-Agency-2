package com.inditex.gym_lorza.repository;

import com.inditex.gym_lorza.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
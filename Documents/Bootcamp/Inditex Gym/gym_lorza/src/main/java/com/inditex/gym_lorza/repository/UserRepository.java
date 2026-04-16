package com.inditex.gym_lorza.repository;

import com.inditex.gym_lorza.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
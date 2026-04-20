package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.exception.UserNotFoundException;
import com.inditex.gym_lorza.model.User;
import com.inditex.gym_lorza.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public User addUser(User newUser) {
        return userRepository.save(newUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        existingUser.setName(updatedUser.getName());
        existingUser.setSurname(updatedUser.getSurname());
        existingUser.setDni(updatedUser.getDni());
        existingUser.setStartYear(updatedUser.getStartYear());
        existingUser.setIsActive(updatedUser.getIsActive());
        existingUser.setImage(updatedUser.getImage());

        return userRepository.save(existingUser);
    }
}
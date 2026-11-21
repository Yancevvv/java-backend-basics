package org.example.service;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }
        User saved = userRepository.save(UserMapper.toEntity(request));
        return UserMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(UserMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse update(Long id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        UserMapper.updateEntity(existing, request);
        User updated = userRepository.save(existing);
        return UserMapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
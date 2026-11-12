package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, int age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must be valid");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
        return userDao.save(new User(name, email, age));
    }

    public Optional<User> getUser(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        User existing = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (name != null && !name.isBlank()) existing.setName(name);
        if (email != null && email.contains("@")) existing.setEmail(email);
        if (age != null && age >= 0) existing.setAge(age);
        return userDao.update(existing);
    }

    public boolean deleteUser(Long id) {
        return userDao.deleteById(id);
    }
}
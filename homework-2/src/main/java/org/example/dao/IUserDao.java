package org.example.dao;

import org.example.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserDao {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    User update(User user);
    boolean deleteById(Long id);
}

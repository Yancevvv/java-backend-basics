package org.example.mapper;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.model.User;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getCreatedAt());
    }

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());
        return user;
    }

    public static void updateEntity(User user, UserRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());
    }
}

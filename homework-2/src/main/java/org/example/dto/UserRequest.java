package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @Min(0) @Max(150) int age
) {}

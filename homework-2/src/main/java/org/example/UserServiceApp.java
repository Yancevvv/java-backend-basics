package org.example;

import org.example.dao.UserDAO;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

public class UserServiceApp {
    private static final UserDAO userDAO = new UserDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== User Service Menu ===");
            System.out.println("1. Create User");
            System.out.println("2. Find User by ID");
            System.out.println("3. Find All Users");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createUser();
                case "2" -> findUserById();
                case "3" -> findAllUsers();
                case "4" -> updateUser();
                case "5" -> deleteUser();
                case "6" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void createUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        try {
            userDAO.save(user);
            System.out.println("User created successfully: " + user);
        } catch (Exception e) {
            System.out.println("Failed to create user: " + e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.print("Enter user ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDAO.findById(id);
        if (user != null) {
            System.out.println("Found: " + user);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void findAllUsers() {
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        System.out.print("Enter user ID to update: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDAO.findById(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter new name (current: " + user.getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Enter new email (current: " + user.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Enter new age (current: " + user.getAge() + "): ");
        String ageStr = scanner.nextLine();
        if (!ageStr.isBlank()) user.setAge(Integer.parseInt(ageStr));

        try {
            userDAO.update(user);
            System.out.println("User updated successfully.");
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.print("Enter user ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            userDAO.delete(id);
            System.out.println("User deleted (if existed).");
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }
}
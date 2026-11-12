package org.example.dao;

import org.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoImplIT {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDaoImpl userDao;

    @BeforeAll
    static void setUpSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.addAnnotatedClass(User.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void tearDownSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory); // передаём SessionFactory явно
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void save_shouldPersistUserAndAssignId() {
        User user = new User("Alice", "alice@example.com", 30);

        User saved = userDao.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice");
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        User saved = userDao.save(new User("Bob", "bob@example.com", 25));

        Optional<User> found = userDao.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = userDao.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userDao.save(new User("User1", "u1@example.com", 20));
        userDao.save(new User("User2", "u2@example.com", 21));

        List<User> users = userDao.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void update_shouldModifyExistingUser() {
        User saved = userDao.save(new User("Charlie", "charlie@example.com", 40));
        saved.setName("Charles");
        saved.setAge(41);

        User updated = userDao.update(saved);

        assertThat(updated.getName()).isEqualTo("Charles");
        assertThat(updated.getAge()).isEqualTo(41);

        Optional<User> reloaded = userDao.findById(saved.getId());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getName()).isEqualTo("Charles");
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User saved = userDao.save(new User("Dave", "dave@example.com", 50));

        boolean deleted = userDao.deleteById(saved.getId());

        assertThat(deleted).isTrue();
        assertThat(userDao.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteById_shouldReturnFalseWhenNotExists() {
        boolean deleted = userDao.deleteById(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    void save_shouldThrowRuntimeExceptionOnConstraintViolation() {
        userDao.save(new User("First", "duplicate@example.com", 20));
        User duplicate = new User("Second", "duplicate@example.com", 21);

        assertThatThrownBy(() -> userDao.save(duplicate))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to save user");
    }
}
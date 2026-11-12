package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveValidUser() {
        User saved = new User("Alice", "alice@example.com", 30);
        saved.setId(1L);
        when(userDao.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser("Alice", "alice@example.com", 30);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void createUser_shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() -> userService.createUser("", "a@b.com", 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");

        verify(userDao, never()).save(any());
    }

    @Test
    void createUser_shouldThrowWhenEmailInvalid() {
        assertThatThrownBy(() -> userService.createUser("Bob", "invalid-email", 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");

        verify(userDao, never()).save(any());
    }

    @Test
    void createUser_shouldThrowWhenAgeOutOfRange() {
        assertThatThrownBy(() -> userService.createUser("Bob", "bob@example.com", 200))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Age");

        verify(userDao, never()).save(any());
    }

    @Test
    void getUser_shouldReturnUserWhenDaoReturnsPresent() {
        User user = new User("Alice", "alice@example.com", 30);
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUser(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
        verify(userDao).findById(1L);
    }

    @Test
    void getUser_shouldReturnEmptyWhenDaoReturnsEmpty() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllUsers_shouldDelegateToDao() {
        List<User> users = List.of(new User("A", "a@b.com", 1), new User("B", "b@c.com", 2));
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        verify(userDao).findAll();
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        User existing = new User("Old", "old@example.com", 20);
        existing.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, "New", "new@example.com", 25);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getAge()).isEqualTo(25);
        verify(userDao).update(existing);
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, "X", "x@y.com", 30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUser_shouldDelegateToDao() {
        when(userDao.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userDao).deleteById(1L);
    }
}
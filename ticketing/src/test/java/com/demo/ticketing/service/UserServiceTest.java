package com.demo.ticketing.service;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.UserRepository;
import com.demo.ticketing.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Tester la fonction loadUserByUsername si user existe")
    public void testLoadUserByUsername_UserExists() {
        String username = "test";
        User user = new User();
        user.setUserName(username);

        when(userRepository.findByUserNameAndDeleted(username, false)).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUserNameAndDeleted(username, false);
    }

    @Test
    @DisplayName("Tester la fonction loadUserByUsername si user n'existe pas")
    public void testLoadUserByUsername_UserNotExists() {
        String username = "test";

        when(userRepository.findByUserNameAndDeleted(username, false)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository).findByUserNameAndDeleted(username, false);
    }

    @Test
    @DisplayName("Tester la fonction getUserById si user existe")
    public void testGetUserById_UserExists() {
        String userId = "be13d331-976d-47ff-bc59-4f0e495e1928";
        User user = new User();
        user.setId(userId);

        when(userRepository.findByIdAndDeleted(userId, false)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository).findByIdAndDeleted(userId, false);
    }

    @Test
    @DisplayName("Tester la fonction getUserById si user n'existe pas")
    public void testGetUserById_UserNotExists() {
        String userId = "be13d331-976d-47ff-bc59-4f0e495e1928";

        when(userRepository.findByIdAndDeleted(userId, false)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
        verify(userRepository).findByIdAndDeleted(userId, false);
    }

    @Test
    @DisplayName("Tester la fonction getUserByUserName si user existe")
    public void testGetUserByUserName_UserExists() {
        String username = "test";
        User user = new User();
        user.setUserName(username);

        when(userRepository.findByUserNameAndDeleted(username, false)).thenReturn(Optional.of(user));

        User result = userService.getUserByUserName(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUserNameAndDeleted(username, false);
    }

    @Test
    @DisplayName("Tester la fonction getUserByUserName si user n'existe pas")
    public void testGetUserByUserName_UserNotExists() {
        String username = "test";

        when(userRepository.findByUserNameAndDeleted(username, false)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUserName(username));
        verify(userRepository).findByUserNameAndDeleted(username, false);
    }

    @Test
    @DisplayName("Tester la fonction saveUser si on a un nouveau user")
    public void testSaveUser_NewUser() {
        User user = new User();
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.saveUser(user);

        assertNotNull(result.getId());
        assertFalse(result.isDeleted());
        assertNotNull(result.getLastUpdate());
        assertTrue(new BCryptPasswordEncoder().matches("password", result.getPassword()));
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Tester la fonction saveUser si user est déjà existé")
    public void testSaveUser_ExistingUser() {
        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");
        user.setPassword("encrypted_password");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.saveUser(user);

        assertEquals("be13d331-976d-47ff-bc59-4f0e495e1928", result.getId());
        assertEquals("encrypted_password", result.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Tester la fonction getAllUsers")
    public void testGetAllUsers() {
        User user1 = new User();
        User user2 = new User();

        when(userRepository.findAllByDeleted(false)).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAllByDeleted(false);
    }

    @Test
    @DisplayName("Tester la fonction mapToEntity")
    public void testMapToEntity() {
        UserDto dto = new UserDto();
        dto.setUserId("be13d331-976d-47ff-bc59-4f0e495e1928");
        dto.setUsername("test");
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        User user = userService.mapToEntity(dto);

        assertEquals("be13d331-976d-47ff-bc59-4f0e495e1928", user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
    }

    @Test
    @DisplayName("Tester la fonction mapToDto")
    public void testMapToDto() {
        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");
        user.setUserName("test");
        user.setEmail("test@example.com");
        user.setPassword("password");

        UserDto dto = userService.mapToDto(user);

        assertEquals("be13d331-976d-47ff-bc59-4f0e495e1928", dto.getUserId());
        assertEquals("test", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password", dto.getPassword());
    }


}

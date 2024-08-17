package com.demo.ticketing.service.Impl;

import com.demo.ticketing.dto.UserDto;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.UserRepository;
import com.demo.ticketing.service.UserService;
import com.demo.ticketing.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserNameAndDeleted(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("votre username n'est pas dans la base de données :" + username));
    }

    @Override
    public Optional<User> getUserById(String id) {
        log.info("user id {}", id);
        return userRepository.findByIdAndDeleted(id, false);
    }

    @Override
    public User getUserByUserName(String username) {
        log.info("username {}", username);
        return userRepository.findByUserNameAndDeleted(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("votre username n'est pas dans la base de données :" + username));
    }

    @Override
    public Optional<User> getUserByIdForPisteAudit(String id) {
        log.info("user id {}", id);
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        log.info("user {}", user);
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(IdGenerator.uuid());
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.setDeleted(false);
            user.setLastUpdate(LocalDateTime.now());
        }
        log.info("user {}", user);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByDeleted(false);
    }


    @Override
    public User mapToEntity(UserDto dto) {
        log.info("user dto {}", dto);
        User user = new User();
        user.setId(dto.getUserId());
        user.setDeleted(dto.isDeleted());
        user.setLastUpdate(dto.getLastUpdate());
        user.setUserName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    @Override
    public UserDto mapToDto(User entity) {
        log.info("user {}", entity);
        return UserDto.builder()
                .userId(entity.getId())
                .lastUpdate(entity.getLastUpdate())
                .deleted(entity.isDeleted())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }

}

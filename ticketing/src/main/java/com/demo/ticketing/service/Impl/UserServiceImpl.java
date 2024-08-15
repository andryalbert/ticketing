package com.demo.ticketing.service.Impl;

import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.UserRepository;
import com.demo.ticketing.service.UserService;
import com.demo.ticketing.utils.IdGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserNameAndDeleted(username,false)
                .orElseThrow(()-> new UsernameNotFoundException("votre username n'est pas dans la base de données :"+username));
    }

    @Override
    public Optional<User> getUserById(String id) {
        log.info("user id {}",id);
        return userRepository.findByIdAndDeleted(id,false);
    }

    @Override
    public User saveUser(@Valid User user) {
        user.setId(IdGenerator.uuid());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByDeleted(false);
    }


}

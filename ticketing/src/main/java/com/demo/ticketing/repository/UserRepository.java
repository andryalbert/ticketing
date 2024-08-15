package com.demo.ticketing.repository;

import com.demo.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserNameAndDeleted(String username, boolean deleted);

    Optional<User> findByIdAndDeleted(String id, boolean deleted);
    List<User> findAllByDeleted(boolean deleted);
}

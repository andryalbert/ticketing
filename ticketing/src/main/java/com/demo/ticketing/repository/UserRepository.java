package com.demo.ticketing.repository;

import com.demo.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

}

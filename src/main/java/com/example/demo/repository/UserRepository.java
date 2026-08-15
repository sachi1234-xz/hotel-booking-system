package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

// Data access layer for the User entity.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}

package com.example.bankcards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankcards.entity.User;


public interface UserRepository  extends JpaRepository<User,Long>{
    Optional<User>findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNUmber);
    Optional<User> findByUsername(String username);
}

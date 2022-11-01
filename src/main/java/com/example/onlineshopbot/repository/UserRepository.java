package com.example.onlineshopbot.repository;

import com.example.onlineshopbot.entity.DbUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<DbUser, Long> {
    Optional<DbUser> findByUsername(String username);

    Optional<DbUser> findByChatId(String chatId);
}

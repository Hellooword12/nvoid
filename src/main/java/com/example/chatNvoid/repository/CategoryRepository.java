package com.example.chatNvoid.repository;

import com.example.chatNvoid.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c " +
            "LEFT JOIN FETCH c.messages m " +
            "LEFT JOIN FETCH m.sender " +  // Используем sender вместо user
            "WHERE c.id = :id")
    Optional<Category> findByIdWithMessagesAndSenders(@Param("id") Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.messages")
    List<Category> findAllWithMessageCount();
}

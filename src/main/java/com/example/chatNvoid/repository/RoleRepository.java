package com.example.chatNvoid.repository;

import com.example.chatNvoid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <User, Long> {
}

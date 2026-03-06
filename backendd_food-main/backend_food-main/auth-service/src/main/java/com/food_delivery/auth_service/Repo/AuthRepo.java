package com.food_delivery.auth_service.Repo;

import com.food_delivery.auth_service.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepo extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
  Boolean existsByUsername(String username);
  Optional<UserEntity>findByEmail(String email);
  Boolean existsByEmail(String email);
}
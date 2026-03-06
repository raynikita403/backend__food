package com.food_deliver.restaurant_service.repositories;

import com.food_deliver.restaurant_service.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface RestaurantRepo extends JpaRepository<RestaurantEntity, Long> {

    Optional<RestaurantEntity> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);


    Optional<RestaurantEntity> findByName(String name);
    
}
package com.food_deliver.restaurant_service.repositories;

import com.food_deliver.restaurant_service.entity.Menu;
import com.food_deliver.restaurant_service.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // Fetch all menus by Restaurant entity
    List<Menu> findByRestaurant(RestaurantEntity restaurant);

    // ✅ Fetch all menus by Restaurant ID (for frontend service)
    List<Menu> findByRestaurantId(Long restaurantId);
}
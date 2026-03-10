package com.food_deliver.restaurant_service.service;

import com.food_deliver.restaurant_service.Security.JwtUtil;
import com.food_deliver.restaurant_service.entity.RestaurantEntity;
import com.food_deliver.restaurant_service.repositories.RestaurantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantRepo restaurantRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// Register restaurant
	public RestaurantEntity register(
			String name,
			String description,
			String timing,
			String location,
			String active,
			Long ownerId,
			MultipartFile image
	) {

		RestaurantEntity restaurant = new RestaurantEntity();

		restaurant.setName(name);
		restaurant.setDescription(description);
		restaurant.setTiming(timing);
		restaurant.setLocation(location);
		restaurant.setStatus("active".equalsIgnoreCase(active));
		restaurant.setOwnerId(ownerId);

		if (image != null && !image.isEmpty()) {
			try {
				restaurant.setImage(image.getBytes());
			} catch (Exception e) {
				throw new RuntimeException("Failed to save image", e);
			}
		}

		return restaurantRepository.save(restaurant);
	}
	// Login → return JWT token
//	public String login(String email, String password) {
//		RestaurantEntity restaurant = restaurantRepository.findByEmail(email)
//				.orElseThrow(() -> new RuntimeException("Invalid email or password"));
//
//		if (!passwordEncoder.matches(password, restaurant.getPassword())) {
//			throw new RuntimeException("Invalid email or password");
//		}
//
//		return jwtUtil.generateToken(restaurant.getEmail());
//	}

	// Fetch all restaurants for frontend (exclude password, convert image to Base64)


	public List<Map<String,Object>> getAllRestaurantsForFrontend() {
		return restaurantRepository.findAll()
				.stream()
				.map(r -> {
					Map<String,Object> map = new HashMap<>();
					map.put("id", r.getId());
					map.put("name", r.getName());

					map.put("description", r.getDescription());
					map.put("timing", r.getTiming());
					map.put("location", r.getLocation());
					map.put("active", r.isStatus() ? "active" : "inactive");
					map.put("imageBase64", r.getImage() != null
							? Base64.getEncoder().encodeToString(r.getImage())
							: null);
					return map;
				})
				.collect(Collectors.toList());
	}
	// Toggle active/inactive status
	public void updateStatus(Long id, String active) {
		RestaurantEntity restaurant = restaurantRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Restaurant not found"));
		restaurant.setStatus("active".equalsIgnoreCase(active));
		restaurantRepository.save(restaurant);
	}

	public Map<String, Object> getRestaurantById(Long id) {
		RestaurantEntity restaurant = restaurantRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Restaurant not found"));

		// Convert to DTO or Map for frontend
		return Map.of(
				"id", restaurant.getId(),
				"name", restaurant.getName(),
				"description", restaurant.getDescription(),
				"timing", restaurant.getTiming(),
				"location", restaurant.getLocation(),
				"active", restaurant.isStatus()
		);
	}
}
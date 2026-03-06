package com.food_deliver.restaurant_service.controller;

import com.food_deliver.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Register restaurant (handles multipart/form-data)
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String timing,
            @RequestParam String location,
            @RequestParam String active,
            @RequestParam Long ownerId,
            @RequestParam(required = false) MultipartFile image
    ) {

        return ResponseEntity.ok(
                restaurantService.register(
                        name,
                        description,
                        timing,
                        location,
                        active,
                        ownerId,
                        image
                )
        );
    }
    // Login restaurant → returns JWT token
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        String token = restaurantService.login(request.getEmail(), request.getPassword());
//        return ResponseEntity.ok(new JwtResponse(token));
//    }

    // 🔹 New endpoint: Fetch all restaurants for admin dashboard
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurantsForFrontend());
    }

    // 🔹 New endpoint: Toggle active/inactive status
    @PutMapping("/status/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        restaurantService.updateStatus(id, request.getActive());
        return ResponseEntity.ok().build();
    }

    // Request class for toggle status
    public static class StatusRequest {
        private String active;
        public String getActive() { return active; }
        public void setActive(String active) { this.active = active; }
    }

    // DTO classes for login
    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class JwtResponse {
        private String token;
        public JwtResponse(String token) { this.token = token; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
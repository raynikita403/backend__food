package com.food_deliver.restaurant_service.controller;

import com.food_deliver.restaurant_service.Security.JwtUtil;
import com.food_deliver.restaurant_service.entity.Menu;
import com.food_deliver.restaurant_service.entity.MenuCategory;
import com.food_deliver.restaurant_service.entity.RestaurantEntity;
import com.food_deliver.restaurant_service.entity.SubCategory;
import com.food_deliver.restaurant_service.repositories.MenuRepository;
import com.food_deliver.restaurant_service.repositories.RestaurantRepo;
import com.food_deliver.restaurant_service.service.MenuService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final RestaurantRepo restaurantRepo;
    private final MenuRepository menuRepository;
    private final JwtUtil jwtUtil;

    // ================== FETCH CATEGORIES ==================
    @GetMapping("/categories")
    public ResponseEntity<List<MenuCategory>> getAllCategories() {
        return ResponseEntity.ok(menuService.getAllCategories());
    }

    // ================== FETCH SUBCATEGORIES ==================
    @GetMapping("/subcategories")
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        return ResponseEntity.ok(menuService.getAllSubCategories());
    }

    // ================== FETCH RESTAURANT PRODUCTS ==================
    @GetMapping("/restaurant/products")
    public ResponseEntity<List<Map<String, Object>>> getRestaurantProducts(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        Long ownerId = jwtUtil.extractUserId(token);

        RestaurantEntity restaurant = restaurantRepo.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        List<Map<String, Object>> menus = menuService.getMenuByRestaurant(restaurant);
        return ResponseEntity.ok().body(menus);
    }
    // ================== ADD PRODUCT ==================
    @PostMapping("/restaurant/products")
    public ResponseEntity<?> addProduct(
            HttpServletRequest request,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Boolean status,
            @RequestParam String category,
            @RequestParam String subCategory,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Invalid Authorization header");
        }

        String token = authHeader.substring(7);

        Long ownerId = jwtUtil.extractUserId(token);

        RestaurantEntity restaurant = restaurantRepo.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        byte[] imageBytes = image != null ? image.getBytes() : null;

        Menu savedMenu = menuService.saveMenu(
                name,
                description,
                price,
                status,
                category,
                subCategory,
                imageBytes,
                restaurant
        );

        return ResponseEntity.ok(savedMenu);
    }

    // ================== GET PRODUCT IMAGE ==================
    @GetMapping("/product-images/{id}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (menu.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(menu.getImage());
    }

    // ================== TOGGLE PRODUCT STATUS ==================
    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        menu.setStatus(!menu.getStatus());

        menuRepository.save(menu);

        return ResponseEntity.ok("Status updated");
    }

    // ================== GET MENU BY RESTAURANT ID (FOR USERS) ==================
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Map<String, Object>>> getMenuByRestaurantId(
            @PathVariable Long restaurantId) {

        RestaurantEntity restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        List<Map<String, Object>> menus = menuService.getMenuByRestaurant(restaurant);

        return ResponseEntity.ok(menus);
    }
}
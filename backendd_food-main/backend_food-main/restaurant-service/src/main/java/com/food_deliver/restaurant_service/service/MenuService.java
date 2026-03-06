package com.food_deliver.restaurant_service.service;

import com.food_deliver.restaurant_service.entity.Menu;
import com.food_deliver.restaurant_service.entity.MenuCategory;
import com.food_deliver.restaurant_service.entity.RestaurantEntity;
import com.food_deliver.restaurant_service.entity.SubCategory;
import com.food_deliver.restaurant_service.repositories.MenuRepository;
import com.food_deliver.restaurant_service.repositories.MenuCategoryRepository;
import com.food_deliver.restaurant_service.repositories.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    // ================== SAVE MENU ITEM ==================
    public Menu saveMenu(String name, String description, Double price, Boolean status,
                         String categoryName, String subCategoryName, byte[] imageBytes,
                         RestaurantEntity restaurant) {

        Menu menu = new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setPrice(price);
        menu.setStatus(status != null ? status : true);
        menu.setRestaurant(restaurant);

        // Set category if exists
        if (categoryName != null) {
            MenuCategory category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            menu.setCategory(category);
        }

        // Set subcategory if exists
        if (subCategoryName != null) {
            SubCategory subCategory = subCategoryRepository.findByName(subCategoryName)
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));
            menu.setSubCategory(subCategory);
        }

        if (imageBytes != null && imageBytes.length > 0) {
            menu.setImage(imageBytes);
        }

        return menuRepository.save(menu);
    }

    // ================== GET MENUS FOR FRONTEND ==================
    public List<Map<String, Object>> getMenuByRestaurant(RestaurantEntity restaurant) {
        return menuRepository.findByRestaurant(restaurant)
                .stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", m.getId());
                    map.put("name", m.getName());
                    map.put("description", m.getDescription());
                    map.put("price", m.getPrice());
                    map.put("status", m.getStatus() ? "available" : "unavailable");
                    map.put("category", m.getCategory() != null ? m.getCategory().getName() : null);
                    map.put("subCategory", m.getSubCategory() != null ? m.getSubCategory().getName() : null);
                    map.put("imageBase64", m.getImage() != null
                            ? Base64.getEncoder().encodeToString(m.getImage())
                            : null);
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ================== TOGGLE MENU STATUS ==================
    public void toggleStatus(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        menu.setStatus(!menu.getStatus());
        menuRepository.save(menu);
    }

    // ================== FETCH ALL CATEGORIES ==================
    public List<MenuCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // ================== FETCH ALL SUBCATEGORIES ==================
    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }
}
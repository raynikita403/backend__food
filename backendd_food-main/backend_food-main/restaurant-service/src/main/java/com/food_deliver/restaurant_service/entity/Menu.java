package com.food_deliver.restaurant_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Boolean status;

    private Double price;   // ✅ Added price field

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private MenuCategory category;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;
}
package com.food_delivery.auth_service.Entity;

import com.food_delivery.auth_service.enumm.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private boolean active=true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @PrePersist
    public void assignDefaultRole() {
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.ROLE_USER);
        }
    }
}

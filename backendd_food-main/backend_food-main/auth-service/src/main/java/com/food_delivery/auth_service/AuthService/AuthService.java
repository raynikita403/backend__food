package com.food_delivery.auth_service.AuthService;


//
//import com.food_delivery.auth_service.Entity.UserEntity;
//import com.food_delivery.auth_service.Repo.AuthRepo;
//import com.food_delivery.auth_service.Util.JwtUtil;
//import com.food_delivery.auth_service.exceptions.UserAlreadyExistsException;
//import com.food_delivery.auth_service.exceptions.UserNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final AuthRepo repository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    public void register(String email, String password) {
//
//        if (repository.findByEmail(email).isPresent()) {
//            throw new UserAlreadyExistsException("User already exists with this email");
//        }
//
//        UserEntity user = new UserEntity();
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(password));
//
//        repository.save(user);
//    }
//
//    public String login(String email, String password) {
//
//        UserEntity user = repository.findByEmail(email)
//                .orElseThrow(() ->
//                        new UserNotFoundException("User not found")
//                );
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new com.food_delivery.auth_service.exceptions.InvalidCredentialsException("Invalid email or password");
//        }
//
//        return jwtUtil.generateToken(email);
//    }
//}

import com.food_delivery.auth_service.Entity.UserEntity;
import com.food_delivery.auth_service.Repo.AuthRepo;
import com.food_delivery.auth_service.enumm.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private AuthRepo authRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public UserEntity registerUser(UserEntity user) {
        if(authRepo.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username is already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return authRepo.save(user);
    }
    public UserEntity login(String username, String password) {

        UserEntity user = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user; // login success
    }
    @PostConstruct
    public void createAdminIfNotExist() {
        String adminEmail = "admin9977@gmail.com";
        String adminUsername = "Admin";

        if (!authRepo.existsByEmail(adminEmail)) {
            UserEntity admin = new UserEntity();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setActive(true);

            // Assign admin role
            Set<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_ADMIN);  // ✅ make sure correct enum import
            admin.setRoles(roles);

            authRepo.save(admin);
            System.out.println("✅ Admin user created!");
        } else {
            System.out.println("ℹ️ Admin user already exists.");
        }
    }
}

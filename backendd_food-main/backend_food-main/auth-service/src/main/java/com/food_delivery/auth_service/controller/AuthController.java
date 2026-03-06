package com.food_delivery.auth_service.controller;

import com.food_delivery.auth_service.AuthService.AuthService;
import com.food_delivery.auth_service.Entity.UserEntity;
import com.food_delivery.auth_service.Repo.AuthRepo;
import com.food_delivery.auth_service.enumm.Role;
import com.food_delivery.auth_service.jwt.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

//
//import com.food_delivery.auth_service.AuthService.AuthService;
//import com.food_delivery.auth_service.dto.AuthRequest;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    //Register Api
//    @PostMapping("/register")
//    public String register(@Valid @RequestBody AuthRequest request) {
//        authService.register(request.getEmail(), request.getPassword());
//        return "REGISTER_SUCCESS";
//    }
//
//   //Login API
//    @GetMapping("/login")
//    public String login(@Valid @RequestBody AuthRequest request) {
//        return authService.login(request.getEmail(), request.getPassword());
//    }
//}
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    @Autowired
//    private AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
//        return ResponseEntity.ok(authService.registerUser(user));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
//
//        String username = loginData.get("username");
//        String password = loginData.get("password");
//
//        UserEntity user = authService.login(username, password);
//
//        return ResponseEntity.ok(user);
//    }
//}
@RestController

@RequestMapping("/api/auth")

public class AuthController {

    @Autowired

    private AuthRepo authRepo;

    @Autowired

    private AuthenticationManager authenticationManager;

    @Autowired

    private JwtUtility jwtUtility;

    @Autowired

    private PasswordEncoder passwordEncoder;

    // ----------------- REGISTER -----------------

    @PostMapping("/register")

    public ResponseEntity<?> registerUser(@RequestBody UserEntity user,

                                          @RequestParam(defaultValue = "USER") String type) {


        if (authRepo.existsByEmail(user.getEmail())) {

            return ResponseEntity.badRequest().body("Email already exists");

        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));


        switch (type.toUpperCase()) {

            case "ADMIN":

                user.setRoles(Set.of(Role.ROLE_ADMIN));

                break;

            case "RESTO":

                user.setRoles(Set.of(Role.ROLE_RESTAURANT));

                break;

            default:

                user.setRoles(Set.of(Role.ROLE_USER));

        }

        authRepo.save(user);

        UserEntity savedUser = authRepo.save(user);
        return ResponseEntity.ok(savedUser);
    }


    @PostMapping("/login")

    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {

        String email = loginData.get("username"); // can also accept "email"

        String password = loginData.get("password");

        try {


            authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(email, password)

            );

        } catch (Exception e) {

            return ResponseEntity.status(401).body("Invalid email or password");

        }

        // Fetch user after authentication

        UserEntity user = authRepo.findByEmail(email)

                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT with roles

        String token = jwtUtility.generateToken(
                user.getEmail(),
                user.getRoles(),
                user.getId()
        );

        // Return token + basic user info (do NOT return password!)

        Map<String, Object> response = Map.of(

                "token", token,

                "email", user.getEmail(),

                "roles", user.getRoles().stream().map(Enum::name).toList()

        );

        return ResponseEntity.ok(response);

    }

}


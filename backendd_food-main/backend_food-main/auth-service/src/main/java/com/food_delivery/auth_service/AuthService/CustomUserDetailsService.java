package com.food_delivery.auth_service.AuthService;

import com.food_delivery.auth_service.Entity.UserEntity;
import com.food_delivery.auth_service.Repo.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CustomUserDetailsService implements UserDetailsService {

//    @Autowired
//
  private  final AuthRepo authRepo;

  public  CustomUserDetailsService(AuthRepo authRepo){
      this.authRepo=authRepo;
  }


    @Override

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = authRepo.findByEmail(email)

                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = userEntity.getRoles()

                .stream()

                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))

                .toList();

        return new org.springframework.security.core.userdetails.User(

                userEntity.getEmail(),

                userEntity.getPassword(),

                authorities

        );

    }

}


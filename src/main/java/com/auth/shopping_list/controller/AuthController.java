package com.auth.shopping_list.controller;

import com.auth.shopping_list.security.CustomUserDetailsService;
import com.auth.shopping_list.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam
                                        String username,
                                        @RequestParam
                                        String password
    ) {
        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    username, password);
            authManager.authenticate(auth);

            UserDetails user = userDetailsService.loadUserByUsername(username);
            String jwt = jwtService.generateAccessToken(user);
            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Неверные учетные данные");
        }
    }
}

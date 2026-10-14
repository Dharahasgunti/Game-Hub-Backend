package com.gamehub.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamehub.backend.entity.User;
import com.gamehub.backend.repository.UserRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "https://your-frontend-domain.vercel.app"})
@RequestMapping("/users")
public class UserController {

private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

public UserController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    if (user.getUsername() == null || user.getUsername().isBlank()
            || user.getEmail() == null || user.getEmail().isBlank()
            || user.getPassword() == null || user.getPassword().length() < 8) {
        return ResponseEntity.badRequest()
                .body(Map.of("message",
                        "Username, email, and a password of at least 8 characters are required."));
    }

    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "An account with this email already exists."));
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "id", savedUser.getId(),
            "username", savedUser.getUsername(),
            "email", savedUser.getEmail(),
            "message", "Registration successful."
    ));
}

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String password = credentials.get("password");

    if (email == null || password == null) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Email and password are required."));
    }

    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null || user.getPassword() == null
            || !passwordEncoder.matches(password, user.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password."));
    }

    return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "message", "Login successful."
    ));
}

}

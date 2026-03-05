package com.project.pricing.controller;

import com.project.pricing.auth.UpdateUserRequest;

import com.project.pricing.auth.AuthenticationResponse;
import com.project.pricing.auth.AuthenticationService;
import com.project.pricing.model.Role;
import com.project.pricing.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(authenticationService.getAllUsers());
    }

    @PatchMapping("/{email}/role")
    public ResponseEntity<AuthenticationResponse> updateUserRole(
            @PathVariable String email,
            @RequestBody Map<String, String> request) {
        Role role = Role.valueOf(request.get("role"));
        return authenticationService.updateUserRole(email, role);
    }

    @PutMapping("/{email}")
    public ResponseEntity<AuthenticationResponse> updateUserDetails(
            @PathVariable String email,
            @RequestBody UpdateUserRequest request) {
        return authenticationService.updateUserDetails(email, request);
    }

    @PostMapping("/temporary")
    public ResponseEntity<AuthenticationResponse> createTemporaryUser(@RequestBody Map<String, String> request) {
        // Logic for creating a user with a temporary password
        // This could be a simplified registration that sets a flag or just a standard
        // registration
        // with a generated password sent back.
        String email = request.get("email");
        String firstname = request.get("firstname");
        String lastname = request.get("lastname");
        String temporaryPassword = request.get("password"); // Or generate one
        Role role = Role.valueOf(request.getOrDefault("role", "USER"));

        // For now, we'll use the existing registration logic but return a specific
        // message
        var registerRequest = com.project.pricing.auth.RegisterRequest.builder()
                .email(email)
                .firstname(firstname)
                .lastname(lastname)
                .password(temporaryPassword)
                .role(role)
                .build();

        AuthenticationResponse response = authenticationService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}

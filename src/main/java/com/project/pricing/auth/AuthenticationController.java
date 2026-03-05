package com.project.pricing.auth;

import com.project.pricing.model.User;
import com.project.pricing.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {
  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }

  // Verification endpoints removed for simplicity unless required
  // Kept minimal implementation as requested ("No Reimplementation of Auth")
  // but fixed to work with current User model

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }

  @GetMapping("/get-all-users")
  public List<User> getAllUsers() {
    return service.getAllUsers();
  }

  @PutMapping("/update-user-role-by-/{role}/{email}")
  public ResponseEntity<AuthenticationResponse> updateUserRole(@PathVariable String email, @PathVariable Role role) {
    return service.updateUserRole(email, role);
  }

  @GetMapping("/get-user-by-/{email}")
  public User getUserByEmail(@PathVariable String email) {
    return service.getUserByEmail(email);
  }

  @GetMapping("/get-all-users-by-/{role}")
  public List<User> getAllUsersByRole(@PathVariable Role role) {
    return service.getAllUsersByRole(role);
  }
}

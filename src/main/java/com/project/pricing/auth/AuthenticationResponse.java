package com.project.pricing.auth;

import com.project.pricing.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;
  private Role role;
  private String email;
  private String message;
  @Builder.Default
  private Boolean isFirstTime = true;
  @Builder.Default
  private Boolean isVerified = true;
  @Builder.Default
  private Boolean hasTwoFactor = false;
}

package com.project.pricing.auth;

import com.project.pricing.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String firstname;
    private String lastname;
    private String physicalAddress;
    private Role role;
    private Boolean enabled;
}

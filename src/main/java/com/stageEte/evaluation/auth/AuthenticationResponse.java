package com.stageEte.evaluation.auth;

import com.stageEte.evaluation.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String firstname;
    private String lastname;
    private Role role;
}

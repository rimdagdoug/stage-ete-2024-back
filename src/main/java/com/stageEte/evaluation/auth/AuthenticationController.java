package com.stageEte.evaluation.auth;

import com.stageEte.evaluation.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private  final  AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return  ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return  ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/managers")
    public ResponseEntity<List<User>> getAllManagers() {
        List<User> managers = service.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/developers")
    public ResponseEntity<List<User>> getAllDevelopers() {
        List<User> developers = service.getAllDevelopers();
        return ResponseEntity.ok(developers);
    }
}

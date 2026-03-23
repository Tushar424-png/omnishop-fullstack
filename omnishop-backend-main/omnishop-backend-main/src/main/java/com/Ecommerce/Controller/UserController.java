package com.Ecommerce.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.Ecommerce.Entity.User;
import com.Ecommerce.Repository.UserRepository;
import com.Ecommerce.Service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce.Service.UserService;
import com.Ecommerce.dto.LoginDTO;
import com.Ecommerce.dto.LoginResponseDTO;
import com.Ecommerce.dto.UserRequest;
import com.Ecommerce.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userservice;
    private final JwtService jwtservice;
    private final UserRepository userRepository;

    public UserController(UserService userservice, JwtService jwtservice,UserRepository userRepository) {
        this.userservice = userservice;
        this.jwtservice = jwtservice;
        this.userRepository=userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse add(@Valid @RequestBody UserRequest userRequest) {
        return userservice.save(userRequest);
    }

    @GetMapping("/getall")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> usereponse() {
        return userservice.getAll();
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO request) {

        boolean isValid = userservice.validateUser(
                request.getEmail(),
                request.getPassword()
        );

        if (!isValid) {
            throw new RuntimeException("Invalid email or password");
        }

        String role = userservice.getRoleByEmail(request.getEmail());

        String token = jwtservice.generateToken(
                request.getEmail(),
                role
        );

        UserResponse user = userservice.getUserByEmail(request.getEmail());

        return new LoginResponseDTO(token, user);
    }


    @GetMapping("/getone/{id}")
    public UserResponse getone(@PathVariable Long id) {
        return userservice.getuser(id);
    }
    @PostMapping("/save-token")
    public ResponseEntity<?> saveToken(
            @RequestBody Map<String, String> body,
            Principal principal
    ) {

        String token = body.get("token");

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        user.setFcmToken(token);
        userRepository.save(user);

        return ResponseEntity.ok("Token Saved");
    }
 // UserController.java mein add karein
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToTopic(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        try {
            // Firebase ko batana ki is token ko 'allUsers' ke messages bhejo
            com.google.firebase.messaging.FirebaseMessaging.getInstance()
                .subscribeToTopic(java.util.Arrays.asList(token), "allUsers");
            System.out.print("hello world");
            return ResponseEntity.ok("Subscribed to allUsers topic");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Subscription failed: " + e.getMessage());
        }
    }

}
package fks.healthhub_backend.controller;

import fks.healthhub_backend.dto.UserAuthDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/auth")
public class AuthController {
    private UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService,
                          @Qualifier("User") UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserAuthDTO signupRequest) {

        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (signupRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Requires a password");
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getPassword());
        userService.saveUser(user);

        return ResponseEntity.ok("User signup was successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserAuthDTO loginRequest) {
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login was successful");
            response.put("user_id", user.getId().toString());
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, String.valueOf(user.getId())).body(user.getId());
        } catch (Exception error) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Failed: " + error.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, "user=; Max-Age=0");
        response.addHeader(HttpHeaders.SET_COOKIE, "user_id=; Max-Age=0");
        return ResponseEntity.ok("You've been logged out");
    }

}

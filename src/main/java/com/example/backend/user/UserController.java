package com.example.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> processSignup(@RequestBody User user) {
        userRepository.save(user);
        return new ResponseEntity<>("User signed up successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> processLogin(@RequestBody User user) {
        if (userService.authenticate(user.getUserId(), user.getPassword())) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }


}
package com.netflixtracker.controller;

import com.netflixtracker.entity.User;
import com.netflixtracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint: POST /api/users (Create)
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            // Keeping the try-catch here to explicitly return 400 Bad Request for email conflict
            User registeredUser = userService.saveUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    // Endpoint: GET /api/users (Read All)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    // Endpoint: GET /api/users/search?name={name} (Find by name for login)
    @GetMapping("/search")
    public ResponseEntity<User> findByName(@RequestParam("name") String name) {
        User user = userService.getUserByName(name);
        return ResponseEntity.ok(user);
    }
    
    // Endpoint: GET /api/users/{id} (Read One)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Endpoint: PUT /api/users/{id} (Update)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Endpoint: DELETE /api/users/{id} (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
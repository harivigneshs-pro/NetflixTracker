package com.netflixtracker.service;

import com.netflixtracker.entity.User;
import com.netflixtracker.repository.UserRepository;
import com.netflixtracker.repository.WatchHistoryRepository; // NEW IMPORT for delete fix
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // NEW IMPORT for delete fix
import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring business service
public class UserService {

    private final UserRepository userRepository;
    private final WatchHistoryRepository historyRepository; // NEW FIELD for delete fix

    // Constructor Injection: UPDATED to include WatchHistoryRepository
    public UserService(UserRepository userRepository, WatchHistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository; // Initialize new field
    }

    /**
     * Saves a new user.
     */
    public User saveUser(User user) {
        // Business Rule: Check if a user with this email already exists (Assumes findByEmail is in UserRepository)
        // if (userRepository.findByEmail(user.getEmail()) != null) {
        //     throw new IllegalArgumentException("Email address already registered.");
        // }
        return userRepository.save(user);
    }

    /**
     * Retrieves all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a single user by ID.
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    /**
     * Returns a user by exact name match.
     */
    public User getUserByName(String name) {
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new IllegalArgumentException("User not found with name: " + name);
        }
        return user;
    }

    /**
     * Updates an existing user's details.
     */
    public User updateUser(Long userId, User updatedDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Update fields if they are provided in the request body
        if (updatedDetails.getName() != null) {
            user.setName(updatedDetails.getName());
        }
        if (updatedDetails.getEmail() != null) {
            user.setEmail(updatedDetails.getEmail());
        }
        if (updatedDetails.getSubscriptionType() != null) {
            user.setSubscriptionType(updatedDetails.getSubscriptionType());
        }
        
        return userRepository.save(user);
    }
    
    // -------------------------------------------------------------------
    // DELETE OPERATION (WITH FOREIGN KEY FIX)
    // -------------------------------------------------------------------

    /**
     * Deletes a user and all associated watch history records (the fix).
     * The deleteByUser method is called from the WatchHistoryRepository.
     */
    @Transactional // Ensures both deletes succeed or fail together
    public void deleteUser(Long userId) {
        // 1. Validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 2. Delete ALL dependent WatchHistory records first (Fixes Foreign Key Error)
        historyRepository.deleteByUser(user); 

        // 3. Delete the parent User record
        userRepository.deleteById(userId);
    }
}
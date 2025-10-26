package com.netflixtracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// Lombok annotations for boilerplate code
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a constructor with no arguments
@AllArgsConstructor // Generates a constructor with all fields

// JPA annotations
@Entity
@Table(name = "users") // Maps this class to the 'users' table in the DB
public class User {
    
    // Primary Key (user_id)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment strategy
   @Column(name = "user_id")
    private Long userId; 

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100) // Ensures email is unique
    private String email;

    @Column(name = "subscription_type", length = 30)
    private String subscriptionType;

    @Column(name = "join_date")
    private LocalDateTime joinDate = LocalDateTime.now(); // Default value set on creation
}
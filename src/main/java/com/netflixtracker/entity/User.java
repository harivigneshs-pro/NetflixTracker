package com.netflixtracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set; // Needed for the WatchHistory relationship

// Lombok annotations for boilerplate code
@Data 
@NoArgsConstructor 
@AllArgsConstructor 

// JPA annotations
@Entity
@Table(name = "users") 
public class User {
    
    // Primary Key (user_id)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "user_id")
    private Long userId; 

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100) 
    private String email;

    @Column(name = "subscription_type", length = 30)
    private String subscriptionType;

    @Column(name = "join_date")
    private LocalDateTime joinDate = LocalDateTime.now(); 

    // --- RELATIONSHIP MAPPING ---
    
    // One User can have Many WatchHistory records.
    // 'mappedBy' points to the 'user' field in the WatchHistory entity.
    // Cascade.ALL ensures that if a User is deleted, their WatchHistory records are also deleted.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WatchHistory> watchHistory;
    
    // NOTE: You will need to create a simple constructor for the CommandLineRunner 
    // and other creation methods if you don't want to rely on the @AllArgsConstructor:
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.joinDate = LocalDateTime.now();
    }
}
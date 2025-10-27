package com.netflixtracker.repository;

import com.netflixtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// The interface extends JpaRepository, specifying the Entity type (User) 
// and the Primary Key type (Long).
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query method based on field name in the Entity (email).
    // Spring generates the SQL query: SELECT * FROM users WHERE email = ?
    User findByEmail(String email);

    // Find user by exact name
    User findByName(String name);
}
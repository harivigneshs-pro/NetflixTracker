package com.netflixtracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "movies") // Maps this class to the 'movies' table in the DB
public class Movie {
    
    // Primary Key (movie_id)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId; 

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 50)
    private String genre;

    @Column(name = "release_year")
    private Integer releaseYear;

    private Integer duration; // Duration in minutes

    // Average rating, which is updated by the service layer
    @Column(name = "rating_avg")
    private Double ratingAvg = 0.0; // Initialize to 0.0

    @Column(name = "content_type", length = 30)
    private String contentType; // e.g., Movie, Series, Documentary

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
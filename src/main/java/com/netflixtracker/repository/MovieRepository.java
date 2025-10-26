package com.netflixtracker.repository;

import com.netflixtracker.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Derived Query Method: Spring creates the SQL query automatically.
    // It translates to: SELECT * FROM movies ORDER BY rating_avg DESC LIMIT 5
    List<Movie> findTop5ByOrderByRatingAvgDesc();
// File: com.netflixtracker.repository.MovieRepository.java (Add this to the interface)

// Finds movies where the title contains the search string (case-insensitive)
List<Movie> findByTitleContainingIgnoreCase(String title);
// File: com.netflixtracker.repository.MovieRepository.java (Add this to the interface)

// Finds movies that exactly match the genre string
List<Movie> findByGenre(String genre);
}
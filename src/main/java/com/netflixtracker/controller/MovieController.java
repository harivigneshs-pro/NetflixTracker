package com.netflixtracker.controller;

import com.netflixtracker.entity.Movie;
import com.netflixtracker.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page; // <--- ADD THIS IMPORT
import org.springframework.data.domain.Pageable; // <--- ADD THIS IMPORT
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    // Constructor Injection
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Endpoint: POST /api/movies (Create)
    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.addMovie(movie); 
        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }

    // Endpoint: GET /api/movies (Read All)
   @GetMapping
public ResponseEntity<Page<Movie>> getAllMovies(Pageable pageable) {
    // Calls the service method with the Pageable details
    Page<Movie> moviesPage = movieService.getAllMovies(pageable);
    return ResponseEntity.ok(moviesPage);
}

    // Endpoint: GET /api/movies/{id} (Read One)
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable("id") Long id) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }
    
    // Extension Endpoint: GET /api/movies/top-rated
    @GetMapping("/top-rated")
    public List<Movie> getTopRatedMovies() {
        return movieService.getTopRatedMovies();
    }

    // Extension Endpoint: GET /api/movies/top-watched
    @GetMapping("/top-watched")
    public List<Movie> getTopWatchedMovies() {
        return movieService.getTopWatchedMovies();
    }

    // Endpoint: PUT /api/movies/{id} (Update)
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movieDetails) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        Movie updatedMovie = movieService.updateMovie(id, movieDetails);
        return ResponseEntity.ok(updatedMovie);
    }

    // Endpoint: DELETE /api/movies/{id} (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
    // File: com.netflixtracker.controller.MovieController.java (Add this to the class)

/**
 * Endpoint: GET /api/movies/search?title=keyword
 * Description: Searches movies by title keyword.
 */
@GetMapping("/search")
public List<Movie> searchMovies(@RequestParam("title") String titleKeyword) {
    return movieService.searchMoviesByTitle(titleKeyword);
}
// File: com.netflixtracker.controller.MovieController.java (Add this to the class)

/**
 * Endpoint: GET /api/movies/filter?genre=Action
 * Description: Filters movies by a specific genre.
 */
@GetMapping("/filter")
public List<Movie> filterMovies(@RequestParam("genre") String genre) {
    return movieService.filterMoviesByGenre(genre);
}
}
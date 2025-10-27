package com.netflixtracker.service;

import com.netflixtracker.entity.Movie;
import com.netflixtracker.repository.MovieRepository;
import com.netflixtracker.repository.WatchHistoryRepository; // Required for delete fix
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional; // Required for delete fix
import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring business service
public class MovieService {

    private final MovieRepository movieRepository;
    private final WatchHistoryRepository historyRepository; // Injected for delete fix

    // Constructor Injection (Updated to include WatchHistoryRepository)
    public MovieService(MovieRepository movieRepository, WatchHistoryRepository historyRepository) {
        this.movieRepository = movieRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * Adds a new movie to the catalog.
     */
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // -------------------------------------------------------------------
    // READ OPERATIONS (PAGINATED, SEARCH, FILTER)
    // -------------------------------------------------------------------

    /**
     * Retrieves all movies from the catalog with pagination and sorting.
     */
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    /**
     * Retrieves a single movie by ID.
     */
    public Movie getMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));
    }

    /**
     * Retrieves the top 5 movies sorted by average rating.
     */
    public List<Movie> getTopRatedMovies() {
        return movieRepository.findTop5ByOrderByRatingAvgDesc();
    }

    /**
     * Retrieves top watched movies (by watch count). Returns up to 5 by default.
     */
    public List<Movie> getTopWatchedMovies() {
        // Use the WatchHistoryRepository aggregation to find top movie ids
        List<Object[]> rows = historyRepository.findTopMoviesByWatchCount(org.springframework.data.domain.PageRequest.of(0,5));
        List<Long> movieIds = rows.stream().map(r -> ((Number) r[0]).longValue()).toList();
        return movieRepository.findAllById(movieIds);
    }

    /**
     * Searches for movies where the title contains the given keyword (case-insensitive).
     */
    public List<Movie> searchMoviesByTitle(String titleKeyword) {
        if (titleKeyword == null || titleKeyword.trim().isEmpty()) {
            return movieRepository.findAll();
        }
        return movieRepository.findByTitleContainingIgnoreCase(titleKeyword);
    }

    /**
     * Filters movies by the exact genre string.
     */
    public List<Movie> filterMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return movieRepository.findAll();
        }
        // NOTE: This assumes findByGenre is defined in MovieRepository
        return movieRepository.findByGenre(genre); 
    }
    
    // -------------------------------------------------------------------
    // UPDATE OPERATION
    // -------------------------------------------------------------------

    /**
     * Updates an existing movie's details.
     */
    public Movie updateMovie(Long movieId, Movie updatedDetails) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

        // Update fields if they are provided in the request body
        if (updatedDetails.getTitle() != null) {
            movie.setTitle(updatedDetails.getTitle());
        }
        if (updatedDetails.getReleaseYear() != null) {
            movie.setReleaseYear(updatedDetails.getReleaseYear());
        }
        
        return movieRepository.save(movie);
    }
    
    // -------------------------------------------------------------------
    // DELETE OPERATION (WITH FOREIGN KEY FIX)
    // -------------------------------------------------------------------

    /**
     * Deletes a movie and all associated watch history records (the fix).
     */
    @Transactional
    public void deleteMovie(Long movieId) {
        // 1. Validation
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

        // 2. Delete ALL dependent WatchHistory records first (Fixes Foreign Key Error)
        historyRepository.deleteByMovie(movie); 

        // 3. Delete the parent Movie record
        movieRepository.deleteById(movieId);
    }
}
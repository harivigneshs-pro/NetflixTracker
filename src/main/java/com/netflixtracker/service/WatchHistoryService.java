package com.netflixtracker.service;

import com.netflixtracker.entity.Movie;
import com.netflixtracker.entity.User;
import com.netflixtracker.entity.WatchHistory;
import com.netflixtracker.repository.MovieRepository;
import com.netflixtracker.repository.UserRepository;
import com.netflixtracker.repository.WatchHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    // CONSTRUCTOR INJECTION: Ensure all repositories are injected
    public WatchHistoryService(WatchHistoryRepository historyRepository, 
                               UserRepository userRepository, 
                               MovieRepository movieRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    // -------------------------------------------------------------------
    // CREATE OPERATION
    // -------------------------------------------------------------------

    @Transactional
    public WatchHistory addWatchRecord(Long userId, Long movieId, Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + movieId));

        WatchHistory history = new WatchHistory();
        history.setUser(user);
        history.setMovie(movie);
        history.setRating(rating);
        history.setWatchDate(LocalDateTime.now());
        
        WatchHistory savedRecord = historyRepository.save(history);
        
        // Update the Movie's average rating after adding a new rating
        updateMovieAverageRating(movieId); 
        
        return savedRecord;
    }

    // -------------------------------------------------------------------
    // READ OPERATION (PAGINATED)
    // -------------------------------------------------------------------

    /**
     * Retrieves all watch history records for a specific user with pagination.
     */
    public Page<WatchHistory> getHistoryByUserId(Long userId, Pageable pageable) {
        // Validate user existence
        userRepository.findById(userId)
                      .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // NOTE: This relies on the updated repository method signature
        return historyRepository.findByUserUserIdOrderByWatchDateDesc(userId, pageable);
    }
    
    // -------------------------------------------------------------------
    // UPDATE OPERATION
    // -------------------------------------------------------------------

    @Transactional
    public WatchHistory updateRating(Long watchId, Integer newRating) {
        if (newRating == null || newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        
        WatchHistory record = historyRepository.findById(watchId)
                .orElseThrow(() -> new IllegalArgumentException("Watch history record not found with ID: " + watchId));
        
        record.setRating(newRating);
        WatchHistory savedRecord = historyRepository.save(record);
        
        // Update the Movie's average rating after changing a rating
        updateMovieAverageRating(record.getMovie().getMovieId()); 
        
        return savedRecord;
    }
    
    // -------------------------------------------------------------------
    // DELETE OPERATION
    // -------------------------------------------------------------------

    @Transactional
    public void deleteWatchRecord(Long watchId) {
        WatchHistory record = historyRepository.findById(watchId)
                .orElseThrow(() -> new IllegalArgumentException("Watch history record not found with ID: " + watchId));
        
        Long movieId = record.getMovie().getMovieId();
        
        historyRepository.deleteById(watchId);
        
        // Update the Movie's average rating after deleting a rating
        updateMovieAverageRating(movieId); 
    }
    
    // -------------------------------------------------------------------
    // INTERNAL HELPER METHOD
    // -------------------------------------------------------------------

    /**
     * Helper method to recalculate and save the average rating for a movie.
     */
    private void updateMovieAverageRating(Long movieId) {
        List<WatchHistory> movieRatings = historyRepository.findByMovieMovieId(movieId);
        
        Double average = movieRatings.stream()
                .mapToInt(WatchHistory::getRating)
                .average()
                .orElse(0.0);
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found during rating update: " + movieId));

        movie.setRatingAvg(average);
        movieRepository.save(movie);
    }
}
package com.netflixtracker.repository;

import com.netflixtracker.entity.Movie; // <--- NEW IMPORT
import com.netflixtracker.entity.User; // <--- NEW IMPORT
import com.netflixtracker.entity.WatchHistory;
import org.springframework.data.domain.Page; // <--- NEW IMPORT
import org.springframework.data.domain.Pageable; // <--- NEW IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional; // <--- NEW IMPORT
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    
    // Used by WatchHistoryService to get all ratings for a movie to calculate average.
    List<WatchHistory> findByMovieMovieId(Long movieId);

    // -------------------------------------------------------------------
    // UPDATED FOR PAGINATION
    // -------------------------------------------------------------------
    
    /**
     * Used by WatchHistoryService to retrieve a user's entire watch history, now paginated.
     * The signature is changed to accept Pageable and return Page<WatchHistory>.
     */
    Page<WatchHistory> findByUserUserIdOrderByWatchDateDesc(Long userId, Pageable pageable);


    // -------------------------------------------------------------------
    // NEW METHODS FOR CASCADE DELETE FIX (Foreign Key Constraint)
    // -------------------------------------------------------------------

    /**
     * Deletes all WatchHistory records associated with a given User.
     * This is required before deleting a User to avoid foreign key errors.
     */
    @Transactional
    void deleteByUser(User user); 

    /**
     * Deletes all WatchHistory records associated with a given Movie.
     * This is required before deleting a Movie to avoid foreign key errors.
     */
    @Transactional
    void deleteByMovie(Movie movie);

    // Returns pairs of [movieId, count] ordered by count desc
    @Query("SELECT w.movie.movieId, COUNT(w) FROM WatchHistory w GROUP BY w.movie.movieId ORDER BY COUNT(w) DESC")
    List<Object[]> findTopMoviesByWatchCount(Pageable pageable);
}
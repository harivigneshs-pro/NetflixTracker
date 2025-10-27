package com.netflixtracker.service;

import com.netflixtracker.entity.Movie;
import com.netflixtracker.entity.Recommendation;
import com.netflixtracker.entity.User;
import com.netflixtracker.repository.MovieRepository;
import com.netflixtracker.repository.RecommendationRepository;
import com.netflixtracker.repository.WatchHistoryRepository;
import com.netflixtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final WatchHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 WatchHistoryRepository historyRepository,
                                 UserRepository userRepository,
                                 MovieRepository movieRepository) {
        this.recommendationRepository = recommendationRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    /**
     * Generate simple recommendations for a user based on most-watched genre.
     * This creates Recommendation records and returns them.
     */
    @Transactional
    public List<Recommendation> generateRecommendationsForUser(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Get user's watch history and determine preferred genre
        List<Long> watchedMovieIds = historyRepository.findByUserUserIdOrderByWatchDateDesc(userId, org.springframework.data.domain.Pageable.unpaged())
                .stream().map(h -> h.getMovie().getMovieId()).collect(Collectors.toList());

        Map<String, Long> genreCount = historyRepository.findByUserUserIdOrderByWatchDateDesc(userId, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(h -> h.getMovie().getGenre())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()));

        if (genreCount.isEmpty()) {
            return Collections.emptyList();
        }

        String topGenre = Collections.max(genreCount.entrySet(), Map.Entry.comparingByValue()).getKey();

        // Find candidate movies in that genre not yet watched
        List<Movie> candidates = movieRepository.findByGenre(topGenre).stream()
                .filter(m -> !watchedMovieIds.contains(m.getMovieId()))
                .limit(limit)
                .collect(Collectors.toList());

        List<Recommendation> recs = new ArrayList<>();
        for (Movie m : candidates) {
            Recommendation r = new Recommendation();
            r.setUser(user);
            r.setMovie(m);
            r.setReason("Based on your interest in " + topGenre);
            r.setCreatedAt(LocalDateTime.now());
            recs.add(r);
        }

        return recommendationRepository.saveAll(recs);
    }

    public List<Recommendation> getRecommendationsForUser(Long userId) {
        return recommendationRepository.findByUserUserId(userId);
    }
}

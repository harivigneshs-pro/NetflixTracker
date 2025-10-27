package com.netflixtracker.controller;

import com.netflixtracker.entity.Recommendation;
import com.netflixtracker.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Recommendation>> getForUser(@PathVariable("id") Long userId) {
        List<Recommendation> recs = recommendationService.getRecommendationsForUser(userId);
        if (recs.isEmpty()) {
            // Generate on demand
            recs = recommendationService.generateRecommendationsForUser(userId, 5);
        }
        return ResponseEntity.ok(recs);
    }
}

package com.netflixtracker.controller;

import com.netflixtracker.entity.WatchHistory;
import com.netflixtracker.service.WatchHistoryService;
import com.netflixtracker.dto.WatchRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;    // <-- Required for pagination return type
import org.springframework.data.domain.Pageable; // <-- Required for pagination input parameter
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class WatchHistoryController {

    private final WatchHistoryService historyService;

    // Constructor Injection
    public WatchHistoryController(WatchHistoryService historyService) {
        this.historyService = historyService;
    }

    public static class RatingUpdate { public Integer rating; public Integer getRating(){return rating;} public void setRating(Integer r){this.rating=r;} }


    // Endpoint: POST /api/history (Create)
    @PostMapping
    public ResponseEntity<WatchHistory> addWatchRecord(@Valid @RequestBody WatchRequestDto request) {
        try {
            // Keeping the try-catch here to explicitly return 400 Bad Request for invalid rating
            WatchHistory record = historyService.addWatchRecord(
                request.getUserId(),
                request.getMovieId(),
                request.getRating()
            );
            return new ResponseEntity<>(record, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("Error-Reason", e.getMessage()).build();
        }
    }

    // -------------------------------------------------------------------
    // FIXED: READ BY USER (PAGINATED)
    // -------------------------------------------------------------------
    
    /**
     * Endpoint: GET /api/history/user/{id}?page=0&size=10
     * Description: View watch history by user with pagination.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Page<WatchHistory>> getHistoryByUser(
            @PathVariable("id") Long userId, 
            Pageable pageable) { // <-- 1. Accepts Pageable object
        
        // 2. Calls the service method with the Pageable object
        Page<WatchHistory> historyPage = historyService.getHistoryByUserId(userId, pageable); 
        
        // 3. Returns the paginated result
        return ResponseEntity.ok(historyPage); 
    }

    // Endpoint: GET /api/history/movie/{id}
    @GetMapping("/movie/{id}")
    public ResponseEntity<List<WatchHistory>> getHistoryByMovie(@PathVariable("id") Long movieId) {
        List<WatchHistory> history = historyService.getHistoryByMovieId(movieId);
        return ResponseEntity.ok(history);
    }
    
    // -------------------------------------------------------------------
    // UPDATE OPERATION
    // -------------------------------------------------------------------
    
    // Endpoint: PUT /api/history/{id} (Update Rating)
    @PutMapping("/{id}")
    public ResponseEntity<WatchHistory> updateRating(
            @PathVariable("id") Long watchId,
            @RequestBody RatingUpdate request) {
        try {
            // Keeping the try-catch here to explicitly return 400 Bad Request for invalid rating/ID
            WatchHistory updatedRecord = historyService.updateRating(
                watchId,
                request.getRating()
            );
            return ResponseEntity.ok(updatedRecord);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("Error-Reason", e.getMessage()).build();
        }
    }

    // -------------------------------------------------------------------
    // DELETE OPERATION
    // -------------------------------------------------------------------

    // Endpoint: DELETE /api/history/{id} (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchRecord(@PathVariable("id") Long watchId) {
        // Global Handler catches IllegalArgumentException (404 Not Found)
        historyService.deleteWatchRecord(watchId);
        return ResponseEntity.noContent().build();
    }
}
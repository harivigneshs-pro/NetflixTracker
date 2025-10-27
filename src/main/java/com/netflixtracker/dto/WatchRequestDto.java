package com.netflixtracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WatchRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long movieId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    public WatchRequestDto() {
    }

    public WatchRequestDto(Long userId, Long movieId, Integer rating) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}

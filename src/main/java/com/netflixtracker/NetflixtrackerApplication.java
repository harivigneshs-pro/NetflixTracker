package com.netflixtracker;

import com.netflixtracker.entity.Movie;
import com.netflixtracker.entity.User;
import com.netflixtracker.entity.WatchHistory;
import com.netflixtracker.repository.MovieRepository;
import com.netflixtracker.repository.UserRepository;
import com.netflixtracker.repository.WatchHistoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

@SpringBootApplication
public class NetflixtrackerApplication {

    /**
     * MANDATORY: This is the missing main method that starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(NetflixtrackerApplication.class, args);
    }

    // Configure CORS to allow the frontend (localhost:8080) to access the backend
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }

    @Bean
    public CommandLineRunner demoData(UserRepository userRepository, MovieRepository movieRepository, WatchHistoryRepository historyRepository) {
        return args -> {
            // 1. Create Users (use unique emails to avoid DB unique constraint violations)
            User alice = new User("Alice", "alice@example.com");
            User bob = new User("Bob", "bob@example.com");
            User admin = new User("Admin", "admin@example.com");

            if (userRepository.findByEmail(alice.getEmail()) == null) userRepository.save(alice);
            if (userRepository.findByEmail(bob.getEmail()) == null) userRepository.save(bob);
            if (userRepository.findByEmail(admin.getEmail()) == null) userRepository.save(admin);

            // 2. Create Movies
            Movie dune = new Movie("Dune", "Sci-Fi", 2021);
            Movie inception = new Movie("Inception", "Sci-Fi", 2010);
            Movie parasite = new Movie("Parasite", "Thriller", 2019);
            Movie madMax = new Movie("Mad Max: Fury Road", "Action", 2015);
            Movie avatar = new Movie("Avatar", "Sci-Fi", 2009);

            movieRepository.save(dune);
            movieRepository.save(inception);
            movieRepository.save(parasite);
            movieRepository.save(madMax);
            movieRepository.save(avatar);

            // 3. Create Watch History
            
        /*  // Alice's history
           /* */// historyRepository.save(new WatchHistory(null, alice, dune, 9, LocalDateTime.now().minusDays(5)));
            /*historyRepository.save(new WatchHistory(null, alice, inception, 10, LocalDateTime.now().minusDays(10)));
            historyRepository.save(new WatchHistory(null, alice, parasite, 8, LocalDateTime.now().minusDays(2)));

            // Bob's history
            historyRepository.save(new WatchHistory(null, bob, dune, 7, LocalDateTime.now().minusDays(7)));
            historyRepository.save(new WatchHistory(null, bob, madMax, 9, LocalDateTime.now().minusDays(3)));

            // Admin's history (to show up in the report)
            historyRepository.save(new WatchHistory(null, admin, dune, 10, LocalDateTime.now().minusDays(1)));
            historyRepository.save(new WatchHistory(null, admin, inception, 9, LocalDateTime.now().minusDays(4)));
            historyRepository.save(new WatchHistory(null, admin, madMax, 8, LocalDateTime.now().minusDays(6)));
            historyRepository.save(new WatchHistory(null, admin, madMax, 8, LocalDateTime.now().minusDays(6)));*/


            System.out.println("\n--- DEMO DATA LOADED SUCCESSFULLY. ---\n");
        };
    }
}

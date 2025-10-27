// api.js - Handles all communication with the Spring Boot REST API
const BASE_URL = 'http://localhost:8080/api';

/**
 * Endpoint: GET /api/users/search?name={name}
 * Purpose: Authenticates user by username and returns User ID.
 */
export const loginUser = async (name) => {
    try {
        const response = await fetch(`${BASE_URL}/users/search?name=${encodeURIComponent(name)}`);
        if (!response.ok) {
            // Handle 404 Not Found if user doesn't exist
            throw new Error(`User "${name}" not found. Status: ${response.status}`);
        }
        return response.json(); // Returns { userId: 1, name: "Alice" }
    } catch (error) {
        console.error("Login API Error:", error);
        throw error;
    }
};

/**
 * Endpoint: GET /api/movies
 * Purpose: Fetches the entire movie catalog.
 */
export const fetchAllMovies = async () => {
    try {
        const response = await fetch(`${BASE_URL}/movies`);
        if (!response.ok) {
            throw new Error('Failed to fetch movie catalog.');
        }
        const json = await response.json();
        // handle Spring Data Page<T> wrapper
        return json.content || json;
    } catch (error) {
        console.error("Catalog API Error:", error);
        throw error;
    }
};

/**
 * Endpoint: GET /api/history?userId={id}
 * Purpose: Fetches the user's personal watch history.
 */
export const fetchUserHistory = async (userId) => {
    try {
        const response = await fetch(`${BASE_URL}/history/user/${userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch user history.');
        }
        return response.json();
    } catch (error) {
        console.error("History API Error:", error);
        throw error;
    }
};

/**
 * Endpoint: POST /api/history
 * Purpose: Submits a new rating/watch history record.
 */
export const postNewRating = async (ratingData) => {
    try {
        const response = await fetch(`${BASE_URL}/history`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(ratingData),
        });
        if (!response.ok) {
            throw new Error('Failed to post new rating.');
        }
        return response.json();
    } catch (error) {
        console.error("Rating API Error:", error);
        throw error;
    }
};

/**
 * Endpoint: GET /api/history/top5
 * Purpose: Fetches the aggregated admin report.
 */
export const fetchTopWatchedReport = async () => {
    try {
        const response = await fetch(`${BASE_URL}/movies/top-watched`);
        if (!response.ok) {
            throw new Error('Failed to fetch top watched report.');
        }
        return response.json();
    } catch (error) {
        console.error("Report API Error:", error);
        throw error;
    }
};
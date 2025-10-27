// app.js - Main SPA logic, routing, and state management

// Import all API functions for communication
import * as api from './api.js';

// Get static elements that are always in index.html
const appContainer = document.getElementById('app-container');
const mainNav = document.getElementById('main-nav');
const welcomeMsg = document.getElementById('welcome-message');

let currentUserId = null;
let currentUserName = null;

// --- State and View Management ---

const updateUI = () => {
    if (currentUserId) {
        // Logged In State
        mainNav.style.display = 'flex';
        welcomeMsg.textContent = `Welcome, ${currentUserName}!`;
        
        // Show/Hide Admin Report button (Simple check for "Admin" role for now)
        const reportBtn = document.getElementById('nav-report');
        if (currentUserName && currentUserName.toLowerCase() === 'admin') {
            reportBtn.style.display = 'block';
        } else {
            reportBtn.style.display = 'none';
        }
    } else {
        // Logged Out State
        mainNav.style.display = 'none';
        welcomeMsg.textContent = '';
        renderLoginView(); // <--- Renders the login form when logged out
    }
};

// --- View Rendering Functions ---

const renderLoginView = () => {
    appContainer.innerHTML = `
        <div id="login-form">
            <h2>Login to Netflix Tracker</h2>
            <input type="text" id="username-input" placeholder="Enter Username (e.g., Alice)">
            <button id="login-btn">Log In</button>
            <p id="login-error" style="color: red;"></p>
        </div>
    `;
    // FIX: Listener attached here AFTER the button is created in the DOM
    document.getElementById('login-btn').addEventListener('click', handleLogin);
};

const renderCatalogView = async () => {
    appContainer.innerHTML = '<h2>Movie Catalog 🎬</h2><p>Loading movies...</p>';
    if (!currentUserId) return; // Basic guard

    try {
        const movies = await api.fetchAllMovies();
        appContainer.innerHTML = '<h2>Movie Catalog 🎬</h2>';
        const list = document.createElement('ul');
        movies.forEach(movie => {
            const item = document.createElement('li');
            // IMPORTANT: Your Movie entity has 'releaseYear', not 'year'
            const yearDisplay = movie.releaseYear || 'N/A';
            
            item.innerHTML = `
                <strong>${movie.title}</strong> (${yearDisplay}) - Genre: ${movie.genre || 'N/A'}
                <button class="rate-btn" data-movie-id="${movie.movieId}" data-movie-title="${movie.title}">Rate/Log Watch</button>
            `;
            list.appendChild(item);
        });
        appContainer.appendChild(list);
        
        // Add listeners for dynamically created rate buttons
        document.querySelectorAll('.rate-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                showRatingForm(e.target.dataset.movieId, e.target.dataset.movieTitle);
            });
        });

    } catch (error) {
        appContainer.innerHTML = `<p style="color: red;">Error loading catalog: ${error.message}</p>`;
    }
};

const renderHistoryView = async () => {
    appContainer.innerHTML = '<h2>My Watch History 📅</h2><p>Loading history...</p>';
    if (!currentUserId) return; // Basic guard

    try {
        // NOTE: Assuming your fetchUserHistory API call is correct
        const response = await api.fetchUserHistory(currentUserId);
        
        // Handle pagination response structure (if using Spring Page<T>)
        const historyData = response.content || response; 

        if (historyData.length === 0) {
            appContainer.innerHTML = '<h2>My Watch History 📅</h2><p>No watch history recorded yet.</p>';
            return;
        }

        let html = '<h2>My Watch History 📅</h2><table><tr><th>Movie</th><th>Rating (1-10)</th><th>Date</th></tr>';
        historyData.forEach(item => {
            const title = item.movie ? item.movie.title : 'Unknown Movie';
            const date = new Date(item.watchDate).toLocaleDateString();
            html += `<tr><td>${title}</td><td>${item.rating}</td><td>${date}</td></tr>`;
        });
        html += '</table>';
        appContainer.innerHTML = html;

    } catch (error) {
        appContainer.innerHTML = `<p style="color: red;">Error loading history: ${error.message}</p>`;
    }
};

const renderReportView = async () => {
    appContainer.innerHTML = '<h2>Admin: Top 5 Watched Movies 📊</h2><p>Generating report...</p>';
    if (currentUserName.toLowerCase() !== 'admin') {
         appContainer.innerHTML = '<h2>Admin: Top 5 Watched Movies 📊</h2><p style="color: red;">Access Denied. Admin privilege required.</p>';
         return;
    }

    try {
        const reportData = await api.fetchTopWatchedReport();
        
        if (reportData.length === 0) {
            appContainer.innerHTML = '<h2>Admin: Top 5 Watched Movies 📊</h2><p>No watch data available for report.</p>';
            return;
        }

        // reportData is a list of Movie objects
        let html = '<h2>Admin: Top 5 Watched Movies 📊</h2><ol>';
        reportData.forEach(movie => {
            const title = movie.title || 'Unknown';
            const count = movie.ratingAvg ? `avg rating ${movie.ratingAvg.toFixed(2)}` : '';
            html += `<li>${title} ${count}</li>`;
        });
        html += '</ol>';
        appContainer.innerHTML = html;

    } catch (error) {
        appContainer.innerHTML = `<p style="color: red;">Error generating report: ${error.message}</p>`;
    }
};

const showRatingForm = (movieId, movieTitle) => {
    appContainer.innerHTML = `
        <h2>Rate: ${movieTitle}</h2>
        <div id="rating-form-details">
            <label for="rating-input">Your Rating (1-5):</label>
            <input type="number" id="rating-input" min="1" max="5" required><br><br>
            <button id="submit-rating-btn" data-movie-id="${movieId}">Submit Rating</button>
            <p id="rating-status"></p>
        </div>
    `;
    document.getElementById('submit-rating-btn').addEventListener('click', handleSubmitRating);
};

// --- Event Handlers ---

const handleLogin = async () => {
    const username = document.getElementById('username-input').value;
    const errorP = document.getElementById('login-error');
    errorP.textContent = '';
    
    if (!username) {
        errorP.textContent = 'Please enter a username.';
        return;
    }

    try {
        const user = await api.loginUser(username);
        currentUserId = user.userId;
        currentUserName = user.name;
        localStorage.setItem('currentUserId', currentUserId);
        localStorage.setItem('currentUserName', currentUserName);
        updateUI();
        renderCatalogView(); // Auto-navigate to catalog after login
    } catch (error) {
        errorP.textContent = 'Login failed. User not found. (Try Alice, Bob, or Admin)';
        console.error("Login failed:", error);
    }
};

const handleLogout = () => {
    currentUserId = null;
    currentUserName = null;
    localStorage.removeItem('currentUserId');
    localStorage.removeItem('currentUserName');
    updateUI();
};

const handleSubmitRating = async (e) => {
    const movieId = e.target.dataset.movieId;
    const rating = document.getElementById('rating-input').value;
    const statusP = document.getElementById('rating-status');

    if (rating < 1 || rating > 10) {
        statusP.textContent = 'Rating must be between 1 and 10.';
        return;
    }

    const ratingData = {
        userId: currentUserId,
        movieId: parseInt(movieId),
        rating: parseInt(rating),
    };

    try {
        await api.postNewRating(ratingData);
        statusP.style.color = 'green';
        statusP.textContent = 'Rating submitted successfully! Redirecting to history...';
        setTimeout(renderHistoryView, 1500); // Redirect after 1.5s
    } catch (error) {
        statusP.style.color = 'red';
        statusP.textContent = 'Failed to submit rating. Check console for API error.';
        console.error("Rating submission failed:", error);
    }
};


// --- Initialization ---

document.addEventListener('DOMContentLoaded', () => {
    // 1. Check local storage for persistent login
    currentUserId = localStorage.getItem('currentUserId');
    currentUserName = localStorage.getItem('currentUserName');
    
    // 2. Set up event listeners for STATIC navigation elements
    //    We use optional chaining (?) to prevent crashing if the header elements aren't 
    //    instantly available, although they should be in index.html.
    document.getElementById('logout-btn')?.addEventListener('click', handleLogout);
    document.getElementById('nav-catalog')?.addEventListener('click', renderCatalogView);
    document.getElementById('nav-history')?.addEventListener('click', renderHistoryView);
    document.getElementById('nav-report')?.addEventListener('click', renderReportView);

    // 3. Update the UI based on the initial state
    //    This is the CRITICAL call. If currentUserId is null (logged out), 
    //    this calls renderLoginView(), which injects the form.
    updateUI(); 

    // 4. If logged in, load the default view (Catalog)
    if (currentUserId) {
        renderCatalogView();
    }
});
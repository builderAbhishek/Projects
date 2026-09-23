// JavaScript to toggle the mobile menu on/off
document.addEventListener('DOMContentLoaded', () => {
    const hamburger = document.querySelector('.hamburger-menu');
    const navLinks = document.querySelector('.nav-links');

    hamburger.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });
});


function searchTopic() {
    var query = document.getElementById("search-bar").value;
    if (query !== "") {
        alert("Searching for: " + query);
        // Replace the alert with actual search functionality in your site
        // For example, you can redirect to a search results page with the query:
        // window.location.href = "/search?query=" + query;
    } else {
        alert("Please enter a topic to search.");
    }
}


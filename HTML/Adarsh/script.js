 // JavaScript for handling the form submission and displaying a success message
 document.getElementById('contactForm').addEventListener('submit', function (e) {
    e.preventDefault();  // Prevent form from submitting
  
    // Show the success message
    document.getElementById('formMessage').style.display = 'block';
  });
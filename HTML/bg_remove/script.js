document.getElementById('imageForm').addEventListener('submit', function(event) {
    event.preventDefault();

    // Show loading message
    document.getElementById('loading').classList.remove('hidden');
    document.getElementById('result').classList.add('hidden');

    const formData = new FormData();
    formData.append('image', document.getElementById('imageInput').files[0]);

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.blob())
    .then(blob => {
        // Hide loading and show result
        document.getElementById('loading').classList.add('hidden');
        document.getElementById('result').classList.remove('hidden');

        // Create a download link for the processed image
        const downloadLink = document.getElementById('downloadLink');
        downloadLink.href = URL.createObjectURL(blob);
    })
    .catch(error => {
        alert('An error occurred while processing the image.');
        console.error(error);
    });
});

function generateBackCard() {
    const joiningDate = formatDate(document.getElementById('joining-date').value);
    const expiryDate = formatDate(document.getElementById('expiry-date').value);

    const canvas = document.getElementById('backCanvas');
    const ctx = canvas.getContext('2d');

    const backCardImage = new Image();
    backCardImage.src = '2.png'; // Back PNG path

    backCardImage.onload = function() {
        canvas.width = backCardImage.width;
        canvas.height = backCardImage.height;
        ctx.drawImage(backCardImage, 0, 0);

        // Joining Date
        ctx.fillStyle = 'black';
        ctx.font = 'bold 100px Arial';
        ctx.fillText(joiningDate, 884, 2605);

        // Expiry Date
        ctx.fillText(expiryDate, 884, 2745);

        // Show download button
        document.getElementById('downloadBackBtn').style.display = 'block';
    };
}

// Format date as dd/mm/yyyy
function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

// Download Back Card
document.getElementById('downloadBackBtn').onclick = function() {
    const canvas = document.getElementById('backCanvas');
    const link = document.createElement('a');
    link.download = `back_id_card.png`; // File name
    link.href = canvas.toDataURL('image/png');
    link.click();
};

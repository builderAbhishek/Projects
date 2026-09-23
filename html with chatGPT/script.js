function playMusic() {
    const music = document.getElementById('background-music');
    if (music.paused) {
        music.play().catch(error => {
            console.error('Error playing audio:', error);
        });
    }
    showPopup();
}

function showPopup() {
    const popup = document.getElementById('popup');
    popup.classList.remove('hidden');
}

function closePopup() {
    const popup = document.getElementById('popup');
    popup.classList.add('hidden');
}

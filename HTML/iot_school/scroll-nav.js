// Function for left button scroll with boundary check
        function scrollLeftBtn() {
            const list = document.querySelector('.scrollable-list');
            // Check if we are at the left edge (don't scroll if already at the edge)
            if (list.scrollLeft > 0) {
                list.scrollBy({
                    left: -200,  // Scroll by 200px (adjust as needed)
                    behavior: 'smooth'
                });
            }
        }

        // Function for right button scroll with boundary check
        function scrollRightBtn() {
            const list = document.querySelector('.scrollable-list');
            // Check if we are at the right edge (don't scroll if already at the edge)
            if (list.scrollLeft < (list.scrollWidth - list.clientWidth)) {
                list.scrollBy({
                    left: 200,  // Scroll by 200px (adjust as needed)
                    behavior: 'smooth'
                });
            }
        }

        let isMouseDown = false;
        let startX;
        let scrollLeftPosition;

        const scrollableList = document.querySelector('.scrollable-list');

        // Prevent drag event from firing when a button is clicked
        let isButtonClicked = false;

        // Enable smooth scrolling when mouse is pressed down and moved
        scrollableList.addEventListener('mousedown', (e) => {
            if (isButtonClicked) return; // Prevent scrolling during button click
            e.preventDefault(); // Prevent default action of text selection
            isMouseDown = true;
            startX = e.pageX - scrollableList.offsetLeft;
            scrollLeftPosition = scrollableList.scrollLeft;
            scrollableList.classList.add('grabbing');  // Change cursor to grabbing
        });

        // Mouse move event: when dragging
        scrollableList.addEventListener('mousemove', (e) => {
            if (!isMouseDown) return;
            const x = e.pageX - scrollableList.offsetLeft;
            const walk = (x - startX) * 2;  // Adjust the multiplier to control scroll speed
            scrollableList.scrollLeft = scrollLeftPosition - walk;
        });

        // Mouse up event: stop dragging
        scrollableList.addEventListener('mouseup', () => {
            isMouseDown = false;
            scrollableList.classList.remove('grabbing');  // Reset cursor
        });

        // Mouse leave event: stop dragging if the mouse leaves the list area
        scrollableList.addEventListener('mouseleave', () => {
            if (isMouseDown) {
                isMouseDown = false;
                scrollableList.classList.remove('grabbing');
            }
        });

        // Touch events for mobile users
        scrollableList.addEventListener('touchstart', (e) => {
            if (isButtonClicked) return; // Prevent scrolling during button click
            e.preventDefault();
            isMouseDown = true;
            startX = e.touches[0].pageX - scrollableList.offsetLeft;
            scrollLeftPosition = scrollableList.scrollLeft;
        });

        scrollableList.addEventListener('touchmove', (e) => {
            if (!isMouseDown) return;
            const x = e.touches[0].pageX - scrollableList.offsetLeft;
            const walk = (x - startX) * 2;  // Adjust the multiplier to control scroll speed
            scrollableList.scrollLeft = scrollLeftPosition - walk;
        });

        scrollableList.addEventListener('touchend', () => {
            isMouseDown = false;
        });

        scrollableList.addEventListener('touchcancel', () => {
            isMouseDown = false;
        });

        // Button click handling to prevent double scrolling
        document.querySelector('.scroll-left').addEventListener('click', (e) => {
            isButtonClicked = true;
            scrollLeftBtn();
            setTimeout(() => isButtonClicked = false, 300);  // Reset after 300ms to avoid interference
        });

        document.querySelector('.scroll-right').addEventListener('click', (e) => {
            isButtonClicked = true;
            scrollRightBtn();
            setTimeout(() => isButtonClicked = false, 300);  // Reset after 300ms to avoid interference
        });

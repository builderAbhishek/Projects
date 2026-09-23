 // Initialize SunEditor
        let editor;
        window.onload = function() {
            editor = SUNEDITOR.create('content', {
                height: 400,
                buttonList: [
                    ['undo', 'redo', 'bold', 'italic', 'underline', 'strike', 'subscript', 'superscript', 'font', 'fontSize', 'formatBlock', 'align', 'list', 'table', 'link', 'image', 'video', 'audio', 'fullScreen', 'codeView']
                ],
                placeholder: 'Start typing your content...',
                imageUploadUrl: '' // Disable image upload API for now
            });
        };

        // Handle image upload and preview (for article image)
        document.getElementById('imageUpload').addEventListener('change', function(e) {
            const file = e.target.files[0];
            const reader = new FileReader();

            reader.onload = function(event) {
                const imageUrl = event.target.result;
                // Show image preview
                const previewImage = document.getElementById('imagePreview');
                previewImage.src = imageUrl;
                previewImage.style.display = 'block';

                // Insert the image into the editor
                editor.insertHTML(`<img src="${imageUrl}" alt="Uploaded Image">`);
            };

            if (file) {
                reader.readAsDataURL(file);
            }
        });

        // Handle meta image upload and preview (for SEO image)
        document.getElementById('metaImageUpload').addEventListener('change', function(e) {
            const file = e.target.files[0];
            const reader = new FileReader();

            reader.onload = function(event) {
                const imageUrl = event.target.result;
                // Show meta image preview
                const metaImagePreview = document.getElementById('metaImagePreview');
                metaImagePreview.src = imageUrl;
                metaImagePreview.style.display = 'block';
            };

            if (file) {
                reader.readAsDataURL(file);
            }
        });

        // Handle form submission
        function submitArticle() {
            const title = document.getElementById('title').value;
            const linkName = document.getElementById('link_name').value;
            const content = editor.getContents();
            const author = document.getElementById('author').value;
            const metaDescription = document.getElementById('meta_description').value;
            const metaTags = document.getElementById('meta_tags').value;
            const articleImage = document.getElementById('imagePreview').src;
            const metaImage = document.getElementById('metaImagePreview').src;

            // Log the data (for demonstration purposes)
            console.log('Title:', title);
            console.log('Link Name (SEO URL):', linkName);
            console.log('Content:', content);
            console.log('Author:', author);
            console.log('Meta Description:', metaDescription);
            console.log('Meta Tags:', metaTags);
            console.log('Article Image URL:', articleImage);
            console.log('Meta Image URL:', metaImage);

            // You can send this data to the server using AJAX or form submission
            alert('Article submitted successfully!');
        }


        // Selecting the sidebar and buttons
const sidebar = document.querySelector(".sidebar");
const sidebarOpenBtn = document.querySelector("#sidebar-open");
const sidebarCloseBtn = document.querySelector("#sidebar-close");
const sidebarLockBtn = document.querySelector("#lock-icon");

// Function to toggle the lock state of the sidebar
const toggleLock = () => {
  sidebar.classList.toggle("locked");
  // If the sidebar is not locked
  if (!sidebar.classList.contains("locked")) {
    sidebar.classList.add("hoverable");
    sidebarLockBtn.classList.replace("bx-lock-alt", "bx-lock-open-alt");
  } else {
    sidebar.classList.remove("hoverable");
    sidebarLockBtn.classList.replace("bx-lock-open-alt", "bx-lock-alt");
  }
};

// Function to hide the sidebar when the mouse leaves
const hideSidebar = () => {
  if (sidebar.classList.contains("hoverable")) {
    sidebar.classList.add("close");
  }
};

// Function to show the sidebar when the mouse enter
const showSidebar = () => {
  if (sidebar.classList.contains("hoverable")) {
    sidebar.classList.remove("close");
  }
};

// Function to show and hide the sidebar
const toggleSidebar = () => {
  sidebar.classList.toggle("close");
};

// If the window width is less than 800px, close the sidebar and remove hoverability and lock
if (window.innerWidth < 800) {
  sidebar.classList.add("close");
  sidebar.classList.remove("locked");
  sidebar.classList.remove("hoverable");
}

// Adding event listeners to buttons and sidebar for the corresponding actions
sidebarLockBtn.addEventListener("click", toggleLock);
sidebar.addEventListener("mouseleave", hideSidebar);
sidebar.addEventListener("mouseenter", showSidebar);
sidebarOpenBtn.addEventListener("click", toggleSidebar);
sidebarCloseBtn.addEventListener("click", toggleSidebar);
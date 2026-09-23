const btn = document.querySelector('.talk');
const content = document.querySelector('.content');
const quotesDiv = document.querySelector('.quotes');
const newsDiv = document.querySelector('.news');
const recipesDiv = document.querySelector('.recipes');
const jokesDiv = document.querySelector('.jokes');

function speak(text) {
    const text_speak = new SpeechSynthesisUtterance(text);
    text_speak.lang = "hi-IN"; // Set the language to Hindi
    text_speak.rate = 0.9;
    text_speak.volume = 1;
    text_speak.pitch = 0.6;
    window.speechSynthesis.speak(text_speak);
}

function wishMe() {
    const day = new Date();
    const hour = day.getHours();

    if (hour >= 0 && hour < 12) {
        speak("सुप्रभात पिताश्री...");
    } else if (hour >= 12 && hour < 17) {
        speak("नमस्ते पिताश्री...");
    } else {
        speak("शुभ संध्या पिताजी...");
    }
}

window.addEventListener('load', () => {
    speak(" आपने बालवीर को उठाने का आदेश दिया है .....");
    wishMe();
});

const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
const recognition = new SpeechRecognition();

recognition.onresult = (event) => {
    const currentIndex = event.resultIndex;
    const transcript = event.results[currentIndex][0].transcript;
    content.textContent = transcript;
    takeCommand(transcript.toLowerCase());
}

btn.addEventListener('click', () => {
    content.textContent = "पिताश्री मुझे सुने दे रहा है....";
    recognition.start();
});

function takeCommand(message) {
    if (message.includes('hello') || message.includes('hello')) {
        speak("नमस्ते पिताश्री, मैं आपकी क्या प्रकार मदद कर सकता हूँ?");
    } else if (message.includes("open google")) {
        window.open("https://google.com", "_blank");
        speak("पिताश्री आपके लिए गूगल खोल रहा हूँ ...");
    } else if (message.includes("open youtube")) {
        window.open("https://youtube.com", "_blank");
        speak("पिताश्री आपके लिए यूट्यूब खोल रहा हूँ...");
    } else if (message.includes("open facebook")) {
        window.open("https://facebook.com", "_blank");
        speak("पिताश्री आपके लिए फेसबुक खोल रहा हूँ...");
    } else if (message.includes('what is') || message.includes('who is') || message.includes('what are')) {
        window.open(`https://www.google.com/search?q=${message.replace(" ", "+")}`, "_blank");
        const finalText = "पिताश्री यहां इंटरनेट पर " + message + " के बारे में कुछ जानकारी मिली है।";
        speak(finalText);
    } else if (message.includes('wikipedia')) {
        window.open(`https://en.wikipedia.org/wiki/${message.replace("wikipedia", "")}`, "_blank");
        const finalText = "पिताश्री यहां विकिपीडिया पर " + message + " के बारे में कुछ जानकारी मिली है।";
        speak(finalText);
    } else if (message.includes('time')) {
        const time = new Date().toLocaleString(undefined, { hour: "numeric", minute: "numeric" });
        speak("समय है " + time);
    } else if (message.includes('date')) {
        const date = new Date().toLocaleString(undefined, { month: "short", day: "numeric" });
        speak("आज की तारीख है " + date);
    } else if (message.includes('calculator')) {
        window.open('Calculator:///');
        speak("पिताश्री आपके लिए कैलकुलेटर खोल रहा हूँ...");
    } else if (message.includes('weather')) {
        getWeather();
    } else if (message.includes('news')) {
        getNews();
    } else if (message.includes('lucifer')) {
        getLuciferQuote();
    } else if (message.includes('joke') || message.includes('jokes')) {
        tellJokeInHindi();
    } else if (message.includes('recipe')) {
        suggestRecipe();
    } else if (message.includes('whatsapp')) {
        sendWhatsAppMessage();
    } else if (message.includes('screenshot')) {
        takeScreenshot();
    } else if (message.includes('camera')) {
        openCamera();
    }  else if (message.includes('play game')) {
        playGame();
    } else if (message.includes('quiz') || message.includes('brainstorming')) {
        startQuiz();
    } else if (message.includes('chalao') && message.includes('youtube')) {
        const song = message.replace("play", "").replace("song", "").trim();
        playSong(song);
    } else {
        handleUnknownCommand(message);
    }
}

function getWeather() {
    fetch('https://api.openweathermap.org/data/2.5/weather?q=yourcity&appid=yourapikey')
        .then(response => response.json())
        .then(data => {
            const temp = data.main.temp - 273.15;
            const weather = data.weather[0].description;
            speak(`वर्तमान तापमान ${temp.toFixed(1)} डिग्री सेल्सियस है और मौसम ${weather} है।`);
        })
        .catch(error => {
            speak("पिताश्री मुझे माफ़ कर दीजिये मैं अभी मौसम की जानकारी नहीं प्राप्त कर सका।");
        });
}

function getNews() {
    fetch('https://newsapi.org/v2/top-headlines?country=in&apiKey=2ba314cb259b4a6b828b19b18d23b576')
        .then(response => response.json())
        .then(data => {
            const articles = data.articles;
            if (articles.length > 0) {
                speak("यहाँ प्रमुख समाचार शीर्षक हैं।");
                newsDiv.innerHTML = '';
                articles.slice(0, 5).forEach((article, index) => {
                    const newsElement = document.createElement('p');
                    newsElement.textContent = `${index + 1}. ${article.title}`;
                    newsDiv.appendChild(newsElement);
                    speak(`${index + 1}. ${article.title}`);
                });
            } else {
                speak("मुझे अभी कोई समाचार अपडेट नहीं मिला।");
            }
        })
        .catch(error => {
            speak("मैं अभी समाचार अपडेट प्राप्त नहीं कर सका।");
        });
}

function getLuciferQuote() {
    fetch('https://hindi-quotes.vercel.app/random')
        .then(response => response.json())
        .then(data => {
            if (data && data.quote) {
                const quote = data.quote;
                speak(quote);
                displayQuote(quote);
            } else {
                speak("पिताश्री मै अभी आपको मोटीवेट नहीं कर सकता।");
            }
        })
        .catch(error => {
            speak("मुझे मोटिवेशनल शायरी प्राप्त करने में समस्या हो रही है।");
        });
}

function displayQuote(quote) {
    const quoteElement = document.createElement('p');
    quoteElement.textContent = quote;
    quotesDiv.innerHTML = '';
    quotesDiv.appendChild(quoteElement);
}

function tellJokeInHindi() {
    fetch('https://hindi-jokes-api.onrender.com/jokes?api_key=4b5966cd3b2f758a501ea8870a1c')
        .then(response => response.json())
        .then(data => {
            if (data && data.jokeContent) {
                const joke = data.jokeContent;
                speak(joke);
                displayJoke(joke);
            } else {
                speak("इस समय मुझे कोई चुटकुले नहीं मिल रहे हैं।");
            }
        })
        .catch(error => {
            speak("इस समय मैं चुटकुले नहीं पा सका।");
        });
}

function displayJoke(joke) {
    const jokeElement = document.createElement('p');
    jokeElement.textContent = joke;
    jokesDiv.innerHTML = '';
    jokesDiv.appendChild(jokeElement);
}

// Function to play a song on YouTube
function playSong(song) {
    const apiKey = 'AIzaSyBUdX_hvV5rvUvYuKDPqsUd_k1ZZdBf8Ag'; // Make sure to replace with your actual API key
    const query = encodeURIComponent(song.trim());
    
    fetch(`https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=${query}&key=${apiKey}`)
        .then(response => response.json())
        .then(data => {
            if (data.items && data.items.length > 0) {
                const videoId = data.items[0].id.videoId;
                const videoUrl = `https://www.youtube.com/watch?v=${videoId}`;
                window.open(videoUrl, "_blank");
                speak(`${song} गीत चला रहा हूँ।`);
            } else {
                speak("मुझे यह गाना नहीं मिला।");
            }
        })
        .catch(error => {
            console.error("Error fetching YouTube data: ", error);
            speak("मुझे गाना ढूंढने में समस्या हो रही है।");
        });
}


function suggestRecipe() {
    const morningRecipes = [
        "पोहा",
        "इडली",
        "उपमा",
        "डोसा",
        "पराठा",
        "पोहा", 
        "उपमा", 
        "दलिया", 
        "इडली", 
        "सेंवई उपमा",
        "मूंग दाल चीला",
        "ब्रेड और आमलेट", 
        "साबूदाना खिचड़ी",
        "अंडा भुर्जी",
        "सैंडविच (सब्जी, पनीर)",
        "बेसन का चीला", 
        "पनीर भुर्जी",
        "मखाना खीर", 
        "कॉर्नफ्लेक्स और दूध",
        "स्मूदी (फ्रूट और योगर्ट)",
        "मूंग दाल का उपमा",
        "ब्रेड पोहा", 
        "नूडल्स (सब्जियों के साथ)",
        "दलिया और दूध"
    ];

    const afternoonRecipes = [
        "दाल तड़का",
        "पनीर बटर मसाला",
        "छोले भटूरे",
        "बिरयानी",
        "राजमा चावल",
        "रोटी और सब्जी", "दाल चावल", "खिचड़ी", "पुलाव", "पनीर भुर्जी", "राजमा चावल", "चोलोरे चावल", "कढ़ी चावल", "मटर पनीर", "बिरयानी", "पास्ता", "मिक्स वेज", "पालक पनीर", "अंडा करी", "चपाती और रायता", "आलू पराठा", "गोभी पराठा", "भरवा पराठा", "चिकन करी", "सोया चाप"
    ];

    const eveningRecipes = [
        "पाव भाजी",
        "दही पुरी",
        "वड़ा पाव",
        "समोसा",
        "आलू टिक्की",
        "पकौड़े", "सूप", "उपमा", "पोहा", "चिवड़ा", "फ्रूट चाट", "सैंडविच", "मसाला कॉर्न", "ढोकला", "बेसन का चीला", "मूंग दाल चीला", "पनीर टिक्का", "बटाटा वडा", "कटलेट", "भेलपुरी", "पाव भाजी", "पोहा कटलेट", "तवा पनीर", "मटर की कचौड़ी", "स्प्राउट्स चाट"
    ];

    const nightRecipes = [
        "खिचड़ी",
        "आलू पराठा",
        "मटर पनीर",
        "छोले कुलचे",
        "बैंगन का भर्ता",
        "रोटी और दाल", "चावल और सब्जी", "पुलाव और रायता", "रोटी और पनीर की सब्जी", "दाल मखनी और नान", "मसूर दाल और चावल", "राजमा चावल", "खिचड़ी और पापड़", "पनीर भुर्जी और पराठा", "कढ़ी चावल", "चपाती और मिक्स वेज", "आलू गोभी और रोटी", "चना मसाला और रोटी", "मटर पनीर और चावल", "पालक पनीर और रोटी", "सोया चाप और चावल", "भिंडी की सब्जी और रोटी", "बैंगन का भरता और रोटी", "चिकन करी और चावल", "फ्राइड राइस और मंचूरियन"
    ];

    const hour = new Date().getHours();
    let recipes = [];

    if (hour >= 0 && hour < 12) {
        recipes = morningRecipes;
    } else if (hour >= 12 && hour < 17) {
        recipes = afternoonRecipes;
    } else if (hour >= 17 && hour < 21) {
        recipes = eveningRecipes;
    } else {
        recipes = nightRecipes;
    }

    const randomRecipe = recipes[Math.floor(Math.random() * recipes.length)];
    speak(`आप इस समय स्वादिष्ट ${randomRecipe} बना कर मजे से खा सकते हैं। अगर बनाने नहीं आता तो मुझसे पूछे में आपको बताऊंगी।`);
    displayRecipe(randomRecipe);
}

function displayRecipe(recipe) {
    const recipeElement = document.createElement('p');
    recipeElement.textContent = recipe;
    recipesDiv.innerHTML = '';
    recipesDiv.appendChild(recipeElement);
}

// Function to send WhatsApp message
function sendWhatsAppMessage() {
    const message = prompt("कृपया संदेश दर्ज करें जिसे आप व्हाट्सएप पर भेजना चाहते हैं:");
    if (message) {window.open(`https://api.whatsapp.com/send?text=${encodeURIComponent(message)}`, "_blank");
        speak("व्हाट्सएप संदेश भेज रहा हूँ...");
    } else {
        speak("कोई संदेश दर्ज नहीं किया गया।");
    }
}

// Function to take a screenshot
function takeScreenshot() {
    html2canvas(document.body).then(canvas => {
        const base64image = canvas.toDataURL("image/png");
        
        // Create a link element to download the screenshot
        const link = document.createElement('a');
        link.href = base64image;
        link.download = 'screenshot.png';
        
        // Append the link to the body and click it to trigger the download
        document.body.appendChild(link);
        link.click();
        
        // Remove the link from the body
        document.body.removeChild(link);

        speak("स्क्रीनशॉट ले लिया गया है और डाउनलोड हो रहा है...");
    }).catch(error => {
        speak("स्क्रीनशॉट लेने में समस्या हो रही है।");
    });
}

// Function to open the camera
function openCamera() {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            const videoElement = document.createElement('video');
            videoElement.srcObject = stream;
            videoElement.play();
            document.body.appendChild(videoElement);
            speak("कैमरा खोल रहा हूँ...");
        })
        .catch(error => {
            speak("मैं कैमरा नहीं खोल सका।");
        });
}

// Function to play a game
function playGame() {
    speak("आपके लिए एक मजेदार गेम खोल रहा हूँ...");
    window.open("https://www.example.com/play-game", "_blank");
}

// Function to start a quiz or brainstorming session
function startQuiz() {
    speak("ब्रेनस्टॉर्मिंग प्रश्नोत्तरी शुरू कर रहा हूँ...");
    // Example questions, you can add more or fetch from an API
    const questions = [
        "भारत का राष्ट्रीय पशु कौन सा है?",
        "ताज महल कहाँ स्थित है?",
        "भारत की राजधानी क्या है?",
        "गणेश चतुर्थी किस महीने में मनाई जाती है?",
        "महात्मा गांधी का पूरा नाम क्या था?"
    ];

    let index = 0;
    function askQuestion() {
        if (index < questions.length) {
            speak(questions[index]);
            index++;
            setTimeout(askQuestion, 10000); // Wait 10 seconds before asking the next question
        } else {
            speak("ब्रेनस्टॉर्मिंग प्रश्नोत्तरी समाप्त हुई।");
        }
    }

    askQuestion();
}

// Function to handle unknown commands
function handleUnknownCommand(command) {
    speak("मुझे माफ़ करना, मैं आपकी बात को समझ नहीं पाया। कृपया दोहराएँ।");
}

// The existing event listener for the 'load' event
window.addEventListener('load', () => {
    speak("Baalveer सो कर उठ रहा है ..");
    wishMe();
});
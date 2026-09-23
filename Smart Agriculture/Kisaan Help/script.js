document.getElementById("voiceBtn").addEventListener("click", () => {
  const recognition = new (window.SpeechRecognition ||
    window.webkitSpeechRecognition)();
  recognition.lang = "hi-IN"; // Set language to Hindi
  recognition.start();

  recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    document.getElementById(
      "voiceOutput"
    ).innerText = `आपने कहा: ${transcript}`;
    respondToQuery(transcript);
  };

  recognition.onerror = (event) => {
    document.getElementById(
      "voiceOutput"
    ).innerText = `त्रुटि हुई: ${event.error}`;
  };
});

function respondToQuery(query) {
  let response = "";
  const lowerQuery = query.toLowerCase(); // Convert query to lowercase for easier matching

  // Handle greetings
  if (
    lowerQuery.includes("नमस्ते") ||
    lowerQuery.includes("हाय") ||
    lowerQuery.includes("हैलो")
  ) {
    response = "नमस्ते! मैं किशान मित्र, आपकी क्या मदद कर सकता हूँ।";
  }
  // Handle current time and date
  else if (lowerQuery.includes("समय") || lowerQuery.includes("तारीख")) {
    const now = new Date();
    const time = now.toLocaleTimeString("hi-IN");
    response = `अब समय है ${time} और आज की तारीख ${date} है।`;
  }
  // Handle news
  else if (lowerQuery.includes("समाचार") || lowerQuery.includes("खबर")) {
    response = "आपको समाचार दिखने के लिए ABP News की वेबसाइट पर ले जा रहा हूँ।";
    document.getElementById("botResponse").innerText = response;
    speakResponse(response);

    // Redirect to ABP News
    setTimeout(() => {
      window.open("https://www.aajtak.in/topic/farmer");
    }, 2000); // Adding a delay to allow the user to hear the response

    return; // Exit the funct
  }
  // Define responses for farmer-related queries
  else if (lowerQuery.includes("मौसम")) {
    response = "वर्तमान मौसम नीचे दिखाया गया है।";
  } else if (lowerQuery.includes("गेहूं कैसे उगाए")) {
    response =
      "गेहूं उगाने के लिए, आपको मिट्टी तैयार करनी चाहिए, एक उपयुक्त किस्म का चयन करना चाहिए, और उचित सिंचाई और उर्वरण सुनिश्चित करना चाहिए।";
  } else if (lowerQuery.includes("पशु कैसे पाले")) {
    response =
      "पशुओं की देखभाल के लिए, आपको सही आहार, साफ-सफाई, और नियमित जांच की जरूरत होती है।";
  } else if (lowerQuery.includes("खेत में कौन सी फसल उगाए")) {
    response =
      "फसल चयन आपकी मिट्टी की गुणवत्ता और मौसम पर निर्भर करता है। स्थानीय कृषि विशेषज्ञ से परामर्श करें।";
  } else {
    // Redirect to Google for unknown queries
    apologizeAndRedirect(lowerQuery);
    return; // Exit the function to prevent the bot response from being shown
  }

  document.getElementById("botResponse").innerText = response;
  speakResponse(response);
}

function speakResponse(response) {
  const utterance = new SpeechSynthesisUtterance(response);
  utterance.lang = "hi-IN"; // Set language to Hindi
  utterance.rate = 0.9;
  utterance.volume = 1;
  utterance.pitch = 0.8;
  window.speechSynthesis.speak(utterance);
}

function apologizeAndRedirect(query) {
  const apologyMessage = `मुझे माफ करें, मैं ${query} के बारे में नहीं जानता हूँ। इसलिए मैं आपको Google पर भेज रहा हूँ।`;

  const utterance = new SpeechSynthesisUtterance(apologyMessage);
  utterance.lang = "hi-IN"; // Set language to Hindi

  // Speak the apology message first
  utterance.onend = () => {
    // Redirect to Google after the message is spoken
    const googleSearchUrl = `https://www.google.com/search?q=${encodeURIComponent(
      query
    )}`;
    window.open(googleSearchUrl, "_blank");
  };

  window.speechSynthesis.speak(utterance);
}

// Google Search
document.getElementById("searchBtn").addEventListener("click", () => {
  const query = document.getElementById("searchQuery").value;
  if (query) {
    const googleSearchUrl = `https://www.google.com/search?q=${encodeURIComponent(
      query
    )}`;
    window.open(googleSearchUrl, "_blank");
  }
});

// Fetch Weather Information
document.addEventListener("DOMContentLoaded", function () {
  const apiUrl =
    "https://api.open-meteo.com/v1/forecast?latitude=26.7606&longitude=83.3732&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,showers,snowfall,cloud_cover,pressure_msl";

  fetch(apiUrl)
    .then((response) => response.json())
    .then((data) => {
      displayWeather(data);
    })
    .catch((error) => {
      console.error("Error fetching weather data:", error);
      document.querySelector(".weather-container").innerHTML =
        "Failed to retrieve weather data.";
    });

  function displayWeather(data) {
    const current = data.current;

    const temperatureElement = document.getElementById("temperature");
    const humidityElement = document.getElementById("humidity");
    const cloudCoverElement = document.getElementById("cloud-cover");
    const conditionElement = document.getElementById("condition");

    // Set weather data
    temperatureElement.textContent = `${current.temperature_2m}°C`;
    humidityElement.textContent = `${current.relative_humidity_2m}%`;
    cloudCoverElement.textContent = `${current.cloud_cover}%`;

    // Define weather conditions based on cloud cover
    const cloudCover = current.cloud_cover; // Correctly retrieve cloud cover value
    let conditionText;

    if (cloudCover >= 80) {
      conditionText = "Overcast";
    } else if (cloudCover >= 50) {
      conditionText = "Cloudy";
    } else if (cloudCover >= 20) {
      conditionText = "Partly Cloudy";
    } else {
      conditionText = "Clear";
    }

    // Update the weather condition element
    conditionElement.textContent = conditionText;
  }
});

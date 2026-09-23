

  document.addEventListener("DOMContentLoaded", function() {
      const apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=26.7606&longitude=83.3732&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,showers,snowfall,cloud_cover,pressure_msl";

      fetch(apiUrl)
          .then(response => response.json())
          .then(data => {
              displayWeather(data);
          })
          .catch(error => {
              console.error('Error fetching weather data:', error);
              document.querySelector('.weather-container').innerHTML = 'Failed to retrieve weather data.';
          });

      function displayWeather(data) {
          const current = data.current;
          
          const temperatureElement = document.getElementById('temperature');
          const humidityElement = document.getElementById('humidity');
          const apparentTemperatureElement = document.getElementById('apparent-temperature');
          const precipitationElement = document.getElementById('precipitation');
          const cloudCoverElement = document.getElementById('cloud-cover');
          const conditionElement = document.getElementById('condition');
          
          // Set weather data
          temperatureElement.textContent = `${current.temperature_2m}°C`;
          humidityElement.textContent = `${current.relative_humidity_2m}%`;
          apparentTemperatureElement.textContent = `${current.apparent_temperature}°C`;
          precipitationElement.textContent = `${current.precipitation}mm`;
          cloudCoverElement.textContent = `${current.cloud_cover}%`;

          // Determine weather condition
          let conditionText;
          if (current.precipitation > 0) {
              conditionText = 'Rainy';
          } else if (current.cloud_cover > 80) {
              conditionText = 'Cloudy';
          } else {
              conditionText = 'Clear';
          }

          conditionElement.textContent = conditionText;
      }
  });


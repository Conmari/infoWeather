package scari.corp.infoWeather.service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import scari.corp.infoWeather.WeatherData;
import scari.corp.infoWeather.model.WeatherRequest;
import scari.corp.infoWeather.repository.WeatherRequestRepository;

@Service
public class WeatherService {
    private final RestTemplate restTemplate;

    @Value("${openweathermap.api.url}")
    private String baseUrl;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Autowired
    private WeatherRequestRepository weatherRequestRepository; 

    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public WeatherData getWeather(Double  latitude, Double  longitude) {

        WeatherData weatherData = null; //

        try {
            String url = buildWeatherUrl(latitude, longitude);
            weatherData = restTemplate.getForObject(url, WeatherData.class);
            if (weatherData != null) {
                WeatherRequest weatherRequest = new WeatherRequest();
                weatherRequest.setLatitude(latitude);
                weatherRequest.setLongitude(longitude);
                weatherRequest.setRequestTime(LocalDateTime.now());
                weatherRequest.setCityName(weatherData.getCityName());
                weatherRequest.setWeatherDescription(weatherData.getWeather()[0].getDescription());
                if (weatherData.getMain() != null) {
                    weatherRequest.setTemperature(weatherData.getMain().getTemperature());
                } else {
                     weatherRequest.setTemperature(null);
                }

                weatherRequestRepository.save(weatherRequest);
            }

        } catch (HttpClientErrorException e) {
            System.err.println("Client error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return null;
        } catch (HttpServerErrorException e) {
            System.err.println("Server error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return null;
        } catch (RestClientException e) {
            System.err.println("REST client error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return null;
        }        
        return weatherData;
    }

    public String getWeatherRequestsAsString() {
        List<WeatherRequest> requests = weatherRequestRepository.findAllByOrderByRequestTimeDesc();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder sb = new StringBuilder();
        for (WeatherRequest request : requests) {
            sb.append("<p>")
                    .append("Время: ").append(request.getRequestTime().format(formatter))
                    .append("| Широта: ").append(df.format(request.getLatitude()))
                    .append(", Долгота: ").append(df.format(request.getLongitude()))
                    .append("| Ответ: ").append(request.getWeatherDescription())
                    .append("| Температура: ").append(df.format(request.getTemperature()))
                    .append("</p>");
        }
        String result = sb.toString();
        return result;
    }

    private String buildWeatherUrl(Double latitude, Double longitude) {
        // Документация по API: https://openweathermap.org/current 
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .toUriString();
    }

}

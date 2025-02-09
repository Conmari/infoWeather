package scari.corp.infoWeather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {
    private final RestTemplate restTemplate;

    @Value("${openweathermap.api.url}")
    private String baseUrl;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public WeatherData getWeather(Double  latitude, Double  longitude) {

        try {
            String url = buildWeatherUrl(latitude, longitude);
            return restTemplate.getForObject(url, WeatherData.class);

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

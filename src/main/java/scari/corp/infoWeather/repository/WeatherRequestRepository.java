package scari.corp.infoWeather.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scari.corp.infoWeather.model.WeatherRequest;

public interface  WeatherRequestRepository extends JpaRepository<WeatherRequest, Long> {
    
}

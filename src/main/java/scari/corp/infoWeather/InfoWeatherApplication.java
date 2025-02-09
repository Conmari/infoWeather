package scari.corp.infoWeather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@SpringBootApplication
@PWA(name = "InfoWeather", shortName = "InfoWeather")
@Theme("my-theme")
public class InfoWeatherApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(InfoWeatherApplication.class, args);
	}

}
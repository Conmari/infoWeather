package scari.corp.infoWeather;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

     /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service
     *            The message service. Automatically injected Spring managed bean.
     */

    public MainView(WeatherService service) {

        NumberField latitude  = new NumberField("Широта долгота");
        latitude.addClassName("bordered");


        NumberField longitude = new NumberField("Долгота долгота");
        longitude.addClassName("bordered");        

        HorizontalLayout coordinatesLayout = new HorizontalLayout();
        coordinatesLayout.add(latitude, longitude);
        coordinatesLayout.addClassName("coordinates");


        // Layout для результатов
        VerticalLayout resultsLayout = new VerticalLayout();

        Button button = new Button("Узнать погоду ", e -> {
            Double latValue = latitude.getValue();
            Double lonValue = longitude.getValue();

            if (latValue != null && lonValue != null) {
                WeatherData weatherData = service.getWeather(latValue, lonValue);

                if (weatherData != null) {
                    H1 descriptionStreet = new H1(weatherData.getWeather()[0].getDescription());
                    // температура по факту
                    int roundedTemperature = (int) Math.round(weatherData.getMain().getTemperature());  
                    // температура по ощущается
                    int roundedFeels_like = (int) Math.round(weatherData.getMain().getFeels_like());
                    
                    Paragraph temperature = new Paragraph( roundedTemperature + " градусов ощущается как " + roundedFeels_like);                   

                    String imagePath;

                    if (roundedTemperature >= -50 && roundedTemperature <= -31) {
                        imagePath = "icons/minus_31.jpg";
                    } else if (roundedTemperature >= -30 && roundedTemperature <= -11) {
                        imagePath = "icons/minus_11.jpg";
                    } else if (roundedTemperature >= -10 && roundedTemperature <= 0) {
                        imagePath = "icons/minus_0.jpg";
                    } else if (roundedTemperature >= 0 && roundedTemperature <= 10) {
                        imagePath = "icons/plus0.jpg";
                    } else if (roundedTemperature >= 11 && roundedTemperature <= 20) {
                        imagePath = "icons/plus11.jpg";
                    } else if (roundedTemperature >= 21 && roundedTemperature <= 50) {
                        imagePath = "icons/plus31.jpg";
                    } else {                        
                        imagePath = "icons/default.jpg";
                    }

                    Image icon = new Image(imagePath, "Иконка");
                    icon.setWidth("500px"); 
                    icon.setHeight("300px");

                    resultsLayout.removeAll();
                    resultsLayout.add(descriptionStreet, temperature, icon);
                } else {
                    resultsLayout.removeAll(); 
                    resultsLayout.add(new Paragraph("Не удалось получить данные о погоде."));
                }
            } else {
                resultsLayout.removeAll(); 
                resultsLayout.add(new Paragraph("Пожалуйста, заполните поля широты и долготы."));
            }
            resultsLayout.addClassName("centered-content");
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);

        addClassName("centered-content");

        add(coordinatesLayout, button, resultsLayout);
    }
    
}

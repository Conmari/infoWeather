package scari.corp.infoWeather;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import scari.corp.infoWeather.service.WeatherService;

@Route
@VaadinSessionScope
public class MainView extends VerticalLayout {
    
    /**
    * Construct a new Vaadin view.
    * <p>
    * Build the initial UI state for the user accessing the application.
    *
    * @param service
    *            The message service. Automatically injected Spring managed bean.
    */

    private Button historyButton;
    private boolean historyVisible = true;


    @Autowired
    private WeatherService service;

    private final Div historyDiv = new Div();

    @Autowired
    public MainView(WeatherService service) {

        this.service = service;    

        NumberField latitude  = new NumberField("Широта");
        latitude.addClassName("bordered");

        NumberField longitude = new NumberField("Долгота");
        longitude.addClassName("bordered");        

        HorizontalLayout coordinatesLayout = new HorizontalLayout();
        coordinatesLayout.add(latitude, longitude);
        coordinatesLayout.addClassName("coordinates");

        VerticalLayout resultsLayout = new VerticalLayout();        

        
        updateHistoryParagraph();

        Button button = new Button("Узнать погоду ", e -> {
            Double latValue = latitude.getValue();
            Double lonValue = longitude.getValue();

            if (latValue != null && lonValue != null) {
                WeatherData weatherData = service.getWeather(latValue, lonValue);

                if (weatherData != null) {
                    H1 descriptionStreet = new H1(weatherData.getWeather()[0].getDescription());
                    descriptionStreet.addClassName("bold-text");
                    // температура по факту
                    int roundedTemperature = (int) Math.round(weatherData.getMain().getTemperature());  
                    // температура по ощущается
                    int roundedFeels_like = (int) Math.round(weatherData.getMain().getFeels_like());
                    
                    Paragraph temperature = new Paragraph( roundedTemperature + " градусов ощущается как " + roundedFeels_like);                   
                    temperature.addClassName("basic-text");

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
            updateHistoryParagraph();
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);

        addClassName("centered-content");

        FlexLayout layout = new FlexLayout();

        Div leftGroup = new Div(coordinatesLayout, button, resultsLayout);  
        leftGroup.addClassName("left-group"); 

        historyButton = new Button("Показать историю");
        historyButton.addClickListener(this::toggleHistoryVisibility);
        historyButton.addClassName("right-button");

        historyDiv.add(historyButton);
        
        historyDiv.addClassName("history-container");
        historyDiv.getElement().getStyle().set("overflow", "auto");

        Div RightGroup = new Div(historyButton, historyDiv);  
        RightGroup.addClassName("RightGroup-group"); 

        layout.add(leftGroup, RightGroup);
                
        toggleHistoryVisibility(null);

        add(layout);
    }

    // Обновление истории
    private void updateHistoryParagraph() {
        String historyText = service.getWeatherRequestsAsString();
                
        H3 historyTitle = new H3("История");
        
        historyTitle.addClassName("history-title");
        
        historyDiv.add(historyTitle);
         
        historyDiv.getElement().setProperty("innerHTML", historyTitle.getElement().getOuterHTML() + historyText); // Добавим заголовок и текст

        historyDiv.addClassName("history-container");
            
    }
    // Отображение блока история
    private void toggleHistoryVisibility(ClickEvent<Button> event) {
        historyVisible = !historyVisible;
        
        if (historyVisible) {
            historyButton.setText("Скрыть историю");
        } else {
            historyButton.setText("Показать историю");
        }
        
        historyDiv.setVisible(historyVisible);
    }

}

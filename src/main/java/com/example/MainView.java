package com.example;

import com.google.gson.Gson;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@PageTitle("Carbon FootPrint Calculator")
@Route("")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")

public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("Carbon-footprint-app");
        TextField distance = new TextField("Distance in Kilometers");
        distance.addThemeName("bordered");

        Button calculate = new Button("Calculate Carbon Footprint");
        calculate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        calculate.addClickShortcut(Key.ENTER);

        Select<String> transportCategory = new Select<>();
        transportCategory.setLabel("Transportation Category");
        transportCategory.setItems("Car", "Van", "MotorBike", "Flight");
        transportCategory.setPlaceholder("Car");

        setHorizontalComponentAlignment(Alignment.AUTO, distance, transportCategory, calculate);

        String types[] = {
                "SmallDieselCar",
                "MediumDieselCar",
                "LargeDieselCar",
                "MediumHybridCar",
                "LargeHybridCar",
                "MediumLPGCar",
                "LargeLPGCar",
                "MediumCNGCar",
                "LargeCNGCar",
                "SmallPetrolVan",
                "LargePetrolVan",
                "SmallDielselVan",
                "MediumDielselVan",
                "LargeDielselVan",
                "LPGVan",
                "CNGVan",
                "SmallPetrolCar",
                "MediumPetrolCar",
                "LargePetrolCar",
                "SmallMotorBike",
                "MediumMotorBike",
                "LargeMotorBike",
                "DomesticFlight",
                "ShortEconomyClassFlight",
                "ShortBusinessClassFlight",
                "LongEconomyClassFlight",
                "LongPremiumClassFlight",
                "LongBusinessClassFlight",
                "LongFirstClassFlight"};

        Select<String> transportType = new Select<>();
        transportType.setLabel("Transportation Type");
        transportType.setPlaceholder("");

        transportCategory.addValueChangeListener(
                event -> {
                    List<String> itemList = new ArrayList<>();
                    for (String s : types) {
                        if (s.endsWith(event.getValue())){
                            itemList.add(s);
                        }
                    }
                    transportType.setItems(itemList);
                });

        Div calcResult = new Div();
        calcResult.setText("");

        calculate.addClickListener(event -> {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://carbonfootprint1.p.rapidapi.com/CarbonFootprintFromCarTravel?distance="
                            + distance.getValue() + "&vehicle=" + transportType.getValue())
                    .get()
                    .addHeader("x-rapidapi-key", "x-rapidapi-key")
                    .addHeader("x-rapidapi-host", "carbonfootprint1.p.rapidapi.com")
                    .build();

            try {
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                CarbonFootprint value = gson.fromJson(response.body().string(), CarbonFootprint.class);
                Notification.show("Success!");
                calcResult.setText("Carbon equivalent: " + value.getCarbonEquivalent());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        add(distance, transportCategory, transportType, calculate, calcResult);
    }
}

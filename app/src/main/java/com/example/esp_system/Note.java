package com.example.esp_system;
import com.google.firebase.firestore.Exclude;

public class Note {
    private double temperature;
    private int humidity;
    private int wind_direction ;

    public Note() {
        //public no-arg constructor needed
        //Nothing Here
    }

    public Note(double temperature, int humidity, int wind_direction) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind_direction = wind_direction;
    }

    @Exclude

    public void setTemperature(double temperature){
        this.temperature = temperature;
    }

    public void setHumidity(int humidity){
        this.humidity = humidity;
    }

    public void setPriority(int priority) {
        this.wind_direction = wind_direction;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getwind_direction() {
        return wind_direction;
    }

}


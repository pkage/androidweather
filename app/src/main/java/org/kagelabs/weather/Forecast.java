package org.kagelabs.weather;
import java.util.Date;
/**
 * Created by nd on 9/20/15.
 */
public class Forecast{
    // instance variables
    protected Date date;
    protected Weather conditions;
    protected double temperature;
    protected double chanceOfPrecipitation;
    protected int number;
    String summary;
    public Forecast(Date date, String conditions, double temperature, double chanceOfPrecipitation, String summary){
        super();
        this.chanceOfPrecipitation = chanceOfPrecipitation;
        this.temperature = temperature;
        this.date = date;
        this.number = this.processConditions(conditions);
    }
    private int processConditions(String conditions){
        if (conditions.equals("clear-day")) {
            this.conditions = Weather.SUNNY;
            return R.drawable.sun;
        }
        else if (conditions.equals("clear-night")) {
            this.conditions = Weather.SUNNY;
            return R.drawable.moon;
        }
        else if (conditions.equals("rain")) {
            this.conditions = Weather.RAINY;
            return R.drawable.rainy;
        }
        else if (conditions.equals("snow")) {
            this.conditions = Weather.SNOW;
            return R.drawable.snowy;
        }
        else if (conditions.equals("sleet")) {
            this.conditions = Weather.SLEET;
            return R.drawable.snow;
        }
        else if (conditions.equals("wind")) {
            this.conditions = Weather.WINDY;
            return R.drawable.snowy;
        }
        else if (conditions.equals("fog")) {
            this.conditions = Weather.FOGGY;
            return R.drawable.day_fog;
        }
        else if (conditions.equals("partly-cloudy-day")) {
            this.conditions = Weather.CLOUDY;
            return R.drawable.partly_cloudy;
        }
        else if (conditions.equals("partly-cloudy-night")) {
            this.conditions = Weather.CLOUDY;
            return R.drawable.night_cloudy;
        }
        return R.drawable.precip;
    }
    // getters and setters
    public int getNumber(){
        return this.number;
    }
    public void setNumber(){
        this.number=number;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Weather getConditions() {
        return this.conditions;
    }
    public void setConditions(Weather conditions){
        this.conditions = conditions;
    }
    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public double getChanceOfPrecipitation() {
        return chanceOfPrecipitation;
    }
    public void setChanceOfPrecipitation(double chanceOfPrecipitation) {
        this.chanceOfPrecipitation = chanceOfPrecipitation;
    }

    public String getSummary() {
        return this.summary;
    }
}

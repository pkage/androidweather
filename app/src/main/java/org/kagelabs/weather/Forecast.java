package org.kagelabs.weather;
import java.util.Date;
/**
 * Created by nd on 9/20/15.
 */
public class Forecast{
    // instance variables
    private Date date;
    private Weather conditions;
    private double temperature;
    private double chanceOfPrecipitation;

    // getters and setters
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
}

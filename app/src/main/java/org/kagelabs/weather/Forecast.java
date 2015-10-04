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
    public Forecast(Date date, Weather weather, double temperature, double chanceOfPrecipitation, int number){
        super();
        this.chanceOfPrecipitation = chanceOfPrecipitation;
        this.temperature = temperature;
        this.date = date;
        this.conditions = conditions;
        this.number = number;
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
}

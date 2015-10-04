package org.kagelabs.weather;

import java.util.Date;

/**
 * Created by nd on 9/21/15.
 */
public class DetailForecast extends Forecast {

    private Date time;
    public Date getTime(){
        return time;
    }
    public void setTime(Date time){

        this.time = time;
    }
    public DetailForecast(Date date, Weather conditions, double temperature, double chanceOfPrecipitation, int number){
        super(date,conditions,temperature,chanceOfPrecipitation,number);
        this.chanceOfPrecipitation = chanceOfPrecipitation;
        this.temperature = temperature;
        this.date = date;
        this.conditions = conditions;
        this.number = number;
    }
}

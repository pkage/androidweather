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
}

package org.kagelabs.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Patrick on 10/4/15.
 */
public class WeatherDecoder {

    // instance variables
    public boolean ready;
    public ArrayList<Forecast> forecasts;
    public String hourly_overview;
    public ArrayList<Forecast> detailForecasts;

    public WeatherDecoder() {
        this.reset();
    }

    private void reset() {
        this.ready = false;
        this.forecasts = new ArrayList<Forecast>();
        this.detailForecasts = new ArrayList<Forecast>();
        this.hourly_overview = "";
    }


    /**
     * decode the json string into some real data
     * <p>there's no reason for this to be this hard. java's an awful language</p>
     * @param data string of data
     * @return a bool on whether it failed or not
     */
    public boolean decode(String data) {
        this.reset();

        try {
            JSONObject forecast = new JSONObject(data);
            JSONObject hourly = forecast.getJSONObject("hourly");
            // pull out the hourly overview string
            this.hourly_overview = hourly.getString("overview");

            // get the hourly data
            JSONArray hourly_data = forecast.getJSONArray("data");
            for (int c = 0; c < hourly_data.length(); c++) {
                JSONObject obj = hourly_data.getJSONObject(c);
                // kill me pls
                this.detailForecasts.add(new Forecast(new Date(obj.getLong("time")), obj.getString("icon"), obj.getDouble("temperature"), obj.getDouble("precipProbability")));
            }

            // get the daily data
            JSONObject daily = forecast.getJSONObject("daily");
            JSONArray daily_data = daily.getJSONArray("data");
            for (int c = 0; c < daily_data.length(); c++) {
                JSONObject obj = hourly_data.getJSONObject(c);

                this.forecasts.add(new Forecast(new Date(obj.getLong("time")), obj.getString("icon"), obj.getDouble("temperatureMax"), obj.getDouble("precipProbability")));
            }


            this.ready = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

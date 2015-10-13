package org.kagelabs.weather;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

// google api client
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OverviewActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static String WEATHER_API_KEY = "d75c746f867f03cfca132110b1638fe4";
    public static String EXTRA_JSON = "org.kagelabs.weather.extrajson";
    private static int NUMBER_OF_OVERVIEW_DAYS = 16;

    // location sucks
    private GoogleApiClient gapi;
    private Location lastKnownLocation;


    // http sucks
    private RequestQueue queue;
    private Cache cache;
    private Network network;
    public String rawJSONResponse;

    private ArrayList<Forecast> myWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        System.out.println("starting app...");
        this.cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        this.network = new BasicNetwork(new HurlStack());
        this.queue = new RequestQueue(this.cache, this.network);
        this.queue.start();

        myWeather = new ArrayList<Forecast>();

        this.buildGoogleApiClient();
        this.gapi.connect();
        addWeather();
        addListView();
        clickCallBack();
    }
    private void clickCallBack(){
        ListView list = (ListView)findViewById(R.id.weatherListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Forecast clickWeather = myWeather.get(position);
                String message = clickWeather.getSummary();
                Toast.makeText(OverviewActivity.this, message, Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void addListView(){
        ArrayAdapter<Forecast> adapter = new myListAdapter();
        ListView list = (ListView) findViewById(R.id.weatherListView);

        if (list == null) {
            System.out.println("head for the hills everything is breaking");
        } else {
            System.out.println("everything is fine");
        }

        list.setAdapter(adapter);
        System.out.println("Finished addListView even though we didn't do jack");
    }

    private void addWeather(){

        Date date = new Date();
        Date date2 = new Date();
        myWeather.add(new Forecast(date, "partly-cloudy-day", 70F, .1,"test"));
        myWeather.add(new Forecast(date2, "rain", 60F, .40, "you should never see this"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        switch (item.getItemId()){
            case R.id.action_hourly:
                openHourly();
                return true;
            case R.id.action_settings:
                openSettings();;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void openHourly(){
        System.out.println("pressed hourly");
        Intent intent = new Intent(this, Hourly.class);
        intent.putExtra(this.EXTRA_JSON, this.rawJSONResponse);
        startActivity(intent);
    }
    private void openSettings(){

    }
    public void execHTTPRequest(StringRequest request) {
        this.queue.add(request);
    }

    public static String getAPICallByCoords(String latitude, String longitude) {
        return "https://api.forecast.io/forecast/" + OverviewActivity.WEATHER_API_KEY + "/" + latitude + "," + longitude;
    }

    public static String getAPICallByCityAndCountryCode(String city, String country, int numberOfDays) {
        return "http://api.openweathermap.com/data/2.5/forecast/daily?q=" + city + "," + country + "&cnt=" + numberOfDays + "&APPID=" + OverviewActivity.WEATHER_API_KEY;
    }





    public void getWeather(String apiurl) {
        StringRequest request = new StringRequest(Request.Method.GET, apiurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setRawJSONResponse(response);
                        System.out.println(response);
                        refreshPage();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("things are broken.");
            }
        });

        this.execHTTPRequest(request);
    }

    public void refreshPage() {
        System.out.println(this.rawJSONResponse);
        WeatherDecoder decoder = new WeatherDecoder();
        decoder.decode(this.rawJSONResponse);
        this.myWeather = decoder.forecasts;
        this.addListView();
        
    }

    public void setRawJSONResponse(String json) {
        this.rawJSONResponse = json;
    }

    private synchronized void buildGoogleApiClient() {
        System.out.println("Starting google api creation");
        this.gapi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        System.out.println("Created the google api.");
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("connected to google apis");
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(this.gapi);
        if (lastKnownLocation != null) {
            String api = OverviewActivity.getAPICallByCoords(String.valueOf(lastKnownLocation.getLatitude()), String.valueOf(lastKnownLocation.getLongitude()));
            System.out.println("Curling " + api);
            this.getWeather(api);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("connection to google apis suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("failed to connect to google apis");
    }

    public static double convertFromKelvinToFahrenheit(double kelvin) {
       kelvin = (kelvin-273.16)*9/5+32;
        return kelvin;
    }

    private class myListAdapter extends ArrayAdapter<Forecast>{
        public myListAdapter(){
            super(OverviewActivity.this,R.layout.item_view,myWeather);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }
            Forecast currentWeather = myWeather.get(position);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.imageView);
            imageView.setImageResource(currentWeather.getNumber());
            TextView description = (TextView) itemView.findViewById(R.id.weatherDescription);

            /*String weekDay="";
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            if (currentWeather.toString() == "Monday") {
                weekDay = "Monday";
            } else if (currentWeather.toString().equals("Tuesday")) {
                weekDay = "Tuesday";
            }else if (currentWeather.toString() == "Wednesday") {
                weekDay = "Wednesday";
            } else if (currentWeather.toString() == "Thursday") {
                weekDay = "Thursday";
            } else if (currentWeather.toString() == "Friday") {
                weekDay = "Friday";
            }else if (currentWeather.toString() == "Saturday") {
                weekDay = "Saturday";
            } else  {
                weekDay = "Sunday";
            }

            for(currentWeather : myWeather) {
                String weekDay;
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

                Calendar calendar = Calendar.getInstance();
                weekDay = dayFormat.format(calendar.getTime());
                String desc = String.format("%s\n%d°F\n%d%%", weekDay, (int) currentWeather.getTemperature(), (int) (currentWeather.getChanceOfPrecipitation() * 100));

                description.setText(desc);
            }*/

            String prettyTimestamp = "";

            // create the pretty timestamp
            String[] arr = currentWeather.getDate().toString().split(" ");
            prettyTimestamp = arr[0] + " " + arr[1] + " " + arr[2];

            String desc = String.format("%s\n%d°F\n%d%%", prettyTimestamp, (int)currentWeather.getTemperature(), (int)(currentWeather.getChanceOfPrecipitation() * 100));

            description.setText(desc);
            return itemView;
        }

    }
}
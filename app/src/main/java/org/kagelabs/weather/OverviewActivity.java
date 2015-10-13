package org.kagelabs.weather;
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


public class OverviewActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static String WEATHER_API_KEY = "d75c746f867f03cfca132110b1638fe4";
    public static String EXTRA_JSON = "org.kagelabs.weather.extrajson";
    public static String SETTINGS_JSON = "settings.json";
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

    private SettingsCache settingsCache;


    public OverviewActivity() {
        System.out.println("Running once...");
    }

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

        this.settingsCache = new SettingsCache();
        try {
            this.settingsCache.read(openFileInput(OverviewActivity.SETTINGS_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.settingsCache.write(openFileOutput(OverviewActivity.SETTINGS_JSON, MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register click callbacks for each entry in the list
     */
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


    /**
     * Initializes the list view
     */
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


    /**
     * Fills some dummy data into myWeather
     */
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
            case R.id.action_refresh:
                this.onConnected(null); // hacky!
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /**
     * Opens the hourly forecasts
     */
    private void openHourly(){
        System.out.println("pressed hourly");
        Intent intent = new Intent(this, Hourly.class);
        intent.putExtra(this.EXTRA_JSON, this.rawJSONResponse);
        startActivity(intent);
    }

    /**
     * opens the settings window
     */
    private void openSettings(){
        System.out.println("clicked settings");
        Intent intent = new Intent(this, Settings.class);
        // cache the settings before we do the thing
        try {
            this.settingsCache.write(openFileOutput(OverviewActivity.SETTINGS_JSON, MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);


    }

    /**
     * Add new request for Volley
     * @param request
     */
    public void execHTTPRequest(StringRequest request) {
        this.queue.add(request);
    }

    /**
     * Generate an API call from some coords and the API key
     * @param latitude
     * @param longitude
     * @return string to curl
     */
    public static String getAPICallByCoords(String latitude, String longitude) {
        return "https://api.forecast.io/forecast/" + OverviewActivity.WEATHER_API_KEY + "/" + latitude + "," + longitude;
    }

    /**
     * Gets the weather and draws it.
     * @param apiurl
     */
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

    /**
     * Refreshes the list view
     */
    public void refreshPage() {
        System.out.println(this.rawJSONResponse);
        WeatherDecoder decoder = new WeatherDecoder();
        decoder.decode(this.rawJSONResponse);
        this.myWeather = decoder.forecasts;
        this.addListView();
        
    }


    /**
     * Caches the JSON response
     * @param json
     */
    public void setRawJSONResponse(String json) {
        this.rawJSONResponse = json;
    }

    /**
     * Initialize the Google API Client
     */
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
        try {
            this.settingsCache.read(openFileInput(OverviewActivity.SETTINGS_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connected to google apis");
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(this.gapi);
        if (lastKnownLocation != null) {
            String api;
            String sysloc = this.settingsCache.get("use_system_location");
            if (sysloc == null || sysloc.equals("true")) {
                System.out.println("\tusing system location");
                api = OverviewActivity.getAPICallByCoords(String.valueOf(lastKnownLocation.getLatitude()), String.valueOf(lastKnownLocation.getLongitude()));
                this.settingsCache.put("lat", String.valueOf(lastKnownLocation.getLatitude()));
                this.settingsCache.put("long", String.valueOf(lastKnownLocation.getLongitude()));
            } else {
                System.out.println("\tnot using system location");
                String latitude = this.settingsCache.get("lat");
                String longitude = this.settingsCache.get("long");
                api = OverviewActivity.getAPICallByCoords(latitude, longitude);
            }
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

    /**
     * custom list adapter to do stuff
     */
    private class myListAdapter extends ArrayAdapter<Forecast>{
        public myListAdapter(){
            super(OverviewActivity.this,R.layout.item_view,myWeather);
        }

        /**
         * view constructor
         * @param position
         * @param convertView
         * @param parent
         * @return a view
         */
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

            String desc = String.format("%s\n%d°F\n%d%%", currentWeather.getDate().toString(), (int)currentWeather.getTemperature(), (int)(currentWeather.getChanceOfPrecipitation() * 100));

            description.setText(desc);

            return itemView;
        }

    }
}
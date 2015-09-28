package org.kagelabs.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends Activity {
    private List<Forecast> myWeather =  new ArrayList<Forecast>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        addWeather();
        addListView();
    }
    private void addListView(){
        ArrayAdapter<Forecast> adapter = new myListAdapter();
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }
    private void addWeather(){
        myWeather.add(new Forecast("Monday","Sunny",70F,10%,R.drawable.sunny));
        myWeather.add(new Forecast("Tuesday","Cloudy",60F,40%,R.drawable.cloudy));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static double convertFromKelvinToFahrenheit(double kelvin) {
       kelvin = kelvin/256;
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
            ImageView imageView=(ImageView)itemView.findViewById(R.id.weatherLabel);
            imageView.setImageResource(currentWeather.getNumber());
            TextView weather
            return itemView
        }
    }
}
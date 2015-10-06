package org.kagelabs.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;


import java.util.ArrayList;

public class Hourly extends Activity {
    private ArrayList<Forecast> myWeather;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intent = getIntent();
        WeatherDecoder decoder = new WeatherDecoder();
        decoder.decode(intent.getStringExtra(OverviewActivity.EXTRA_JSON));
        this.myWeather = decoder.detailForecasts;

        ArrayAdapter<Forecast> adapter = new myListAdapter();
        ListView list = (ListView) findViewById(R.id.weatherListView);
        list.setAdapter(adapter);
        clickCallBack();

    }

    private void clickCallBack(){
        ListView list = (ListView)findViewById(R.id.weatherListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Forecast clickWeather = myWeather.get(position);
                String message = clickWeather.getSummary();
                Toast.makeText(Hourly.this, message, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hourly, menu);
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


    private class myListAdapter extends ArrayAdapter<Forecast> {
        public myListAdapter(){
            super(Hourly.this,R.layout.item_view,myWeather);
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

            String desc = String.format("%s\n%d°F\n%d%%", currentWeather.getDate().toString(), (int)currentWeather.getTemperature(), (int)(currentWeather.getChanceOfPrecipitation() * 100));

            description.setText(desc);

            return itemView;
        }

    }

}

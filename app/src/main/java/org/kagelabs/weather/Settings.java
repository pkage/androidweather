package org.kagelabs.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class Settings extends Activity {

    SettingsCache cache;
    CheckBox use_system_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cache = new SettingsCache();
        try {
            cache.read(openFileInput(OverviewActivity.SETTINGS_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
        use_system_location = (CheckBox) findViewById(R.id.checkBox);

        // set the initial state of the checkbox

        String sysloc = cache.get("use_system_location");


        use_system_location.setChecked(sysloc == null || sysloc.equals("true"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            cache.read(openFileInput(OverviewActivity.SETTINGS_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("writing out the settings file");
        try {
            cache.write(openFileOutput(OverviewActivity.SETTINGS_JSON, MODE_PRIVATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void openMaps(View view) {
        Intent intent = new Intent(this, PositionSelector.class);
        startActivity(intent);
    }

    public void toggleSysLocationState(View view) {
        cache.putbool("use_system_location", use_system_location.isChecked());
    }
}

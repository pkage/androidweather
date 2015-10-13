package org.kagelabs.weather;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PositionSelector extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SettingsCache cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new SettingsCache();
        System.out.println("reading settings cache");
        try {
            this.cache.read(openFileInput(OverviewActivity.SETTINGS_JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_position_selector);
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        // Add a marker in Sydney, Australia, and move the camera.
        String _long = this.cache.get("long");
        String _lat = this.cache.get("lat");
        if (_long == null || _lat == null) {
            System.out.println("either lat or long are null");
        } else {
            System.out.println("everything's fine");
        }
        double longitude = Double.valueOf(_long);
        double latitude = Double.valueOf(_lat);
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(location).title("Weather Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                cache.put("lat", String.valueOf(latLng.latitude));
                cache.put("long", String.valueOf(latLng.longitude));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Weather Location"));
                System.out.println("added new marker");
                try {
                    cache.write(openFileOutput(OverviewActivity.SETTINGS_JSON, MODE_PRIVATE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

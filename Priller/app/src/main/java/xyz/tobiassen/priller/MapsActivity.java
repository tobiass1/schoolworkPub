package xyz.tobiassen.priller;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

/**
 * Classactivity that takes care of setting up the map for viewfragment.
 * Modified code from source: http://blog.teamtreehouse.com/beginners-guide-location-android
 */
public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap nMap;
    private GoogleApiClient mapClient;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public final static String LOCATION_OBJECT = "Object.currentLocation";
    private LocationRequest mLocationRequest;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);        // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000); // 1 second, in milliseconds

        setUpMapIfNeeded();
    }


    /**
     * Sets up a new map if the map does not exist.
     */
    private void setUpMapIfNeeded(){
        if (nMap == null) {
            nMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    /**
     * connects to client.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mapClient.connect();
    }

    /**
     * default creates menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    /**
     * default handles the menu item selected.
     * @param item
     * @return
     */
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

    /**
     * Function requests lastlocation, if not found, requesting locationupdates
     * Trying to get the current lat/lon.
     * clears the map for exiting garbage
     * puts the location as a marker on the map, then zooms the camera with the focus of the maker.
     * Then fills a tiny information textview with lat/long information on the current location.
     */
    public void getCurrentLocation(View v){
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mapClient);
        if(currentLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mapClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(currentLocation);
            try {
                double currentLongitude = currentLocation.getLongitude();
                double currentLatitude = currentLocation.getLatitude();
                LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                nMap.clear();
                nMap.addMarker(new MarkerOptions().position(currentLatLng));
                nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            TextView textView = (TextView) findViewById(R.id.location_info);
            String locationInformation = "Longitude: " + String.valueOf(currentLocation.getLongitude())
                    + "\nLatitude: " + String.valueOf(currentLocation.getLatitude());
            textView.setText(locationInformation);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to get location data.\nPlease enable location services", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * If user pushes button to display additional infomration on location: creates intent
     * and puts the currentLocation object into the intent and starting it.
     */
    public void displayLocationInformation(View v) {
        if(currentLocation != null) {
            Intent i = new Intent(this, DisplayLocationInformation.class);
            i.putExtra(LOCATION_OBJECT, currentLocation);
            startActivity(i);
        }
        else Toast.makeText(this, "Unable to display location data without your location\nPlease fetch location first", Toast.LENGTH_SHORT).show();
    }

    /**
     * Log if connected.
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
    findViewById(R.id.location_fetch).setEnabled(true);
        Log.i(TAG, "Location services connected");
    }

    /**
     * Logging if connetion is lost.
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended, please reconnect");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void handleNewLocation(Location currentLocation) {
        Log.d(TAG, currentLocation.toString());
    }

    /**
     * Obligatory function, not used therefore empty.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
    }
}

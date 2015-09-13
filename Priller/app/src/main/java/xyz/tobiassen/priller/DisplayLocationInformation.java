package xyz.tobiassen.priller;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
  This activity is responsible for displaying the information linked to the current location where
  the user is located.

  It also calls the class for displaying temperature on the current location.
 */

@SuppressWarnings("WeakerAccess")
public class DisplayLocationInformation extends AppCompatActivity {
    // Make the location public for availability from WeatherHelper.
    private Location currentLocation;
    private double currentLat;
    private double currentLon;
    private double currentAlt;

    /**
     * scoping the view,
     * Fetching the intent it was called from
     * Placing the Location object from the intent
     * inside the currentLocation object
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_location_information);
        Intent i = getIntent();
        currentLocation = i.getParcelableExtra(MapsActivity.LOCATION_OBJECT);
        currentLat = currentLocation.getLatitude();
        currentLon = currentLocation.getLongitude();
        currentAlt = currentLocation.getAltitude();

        informationDisplay();

    }

    /**
     * The function formats the output into a string and sets it to the correct textview.
     * Also starts the weatherHelper class that outputs temperature.
     */

    private void informationDisplay() {
        String[] addressInformation = getLocationName();
        String information =
                "Longitude: " + currentLon
                        + "\nLatitude: " + currentLat
                        + "\nAltitude: " + currentAlt
                        + "\nAddress: " + addressInformation[4]
                        + "\nCity: " + addressInformation[1]
                        + ", " + addressInformation[3]
                        + "\nCountry: " + addressInformation[2];

        TextView textView = (TextView) findViewById(R.id.displayLocationInformation);
        textView.setText(information);
        showTemperature();


    }

    /**
     * Function uses Geocoder to fetch infomration on the City, Addess, Country and Postalcode
     * on the current location where the user is located.
     * Retruns an array with information about the location.
     */
    private String[] getLocationName() {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        String[] result = new String[5];
        try {
            addresses = geocoder.getFromLocation(this.currentLocation.getLatitude(), this.currentLocation.getLongitude(), 1);
            result[1] = addresses.get(0).getLocality();
            result[2] = addresses.get(0).getCountryName();
            result[3] = addresses.get(0).getPostalCode();
            result[4] = addresses.get(0).getAddressLine(0);
        } catch (IOException ioException) {
            result[1] = "Unable to connect to location service!";
        }
        return result;
    }

    private void showTemperature() {
        String stringUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" +
                currentLat + "&lon=" +
                currentLon;
        TextView textView = (TextView) findViewById(R.id.weatherData);
        if (canConnect()) {
            // calls AsyncTask.
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            textView.setText("No network connection available.");
        }
    }

    /**
     * Opening Connectivitymanager to check if it is possible to establish a connection.
     * @return
     */
    private boolean canConnect() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }


    /**
     * Nearly 100% copy/paste code.
     * Source: http://developer.android.com/training/basics/network-ops/connecting.html
     */

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        /**
         * onPostExecute displays the results of the AsyncTask.
         * This Json handling was created by Levi Tobiassen with help from Olafur Trollebo
         * The temperature that is returned uses default Kelvin. Had to convert to Celsius.
          */

        @Override
        protected void onPostExecute(String result) {
            try {

                JSONObject jsonObject = new JSONObject(result);

                double temp = jsonObject.getJSONObject("main").getDouble("temp");
                double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                temp -= 273.15;
                TextView textView = (TextView) findViewById(R.id.weatherData);
                textView.setText("Temperature: " + String.valueOf(temp) + "Celcius");
                textView.append("\nHumidity: " + String.valueOf(humidity) + "%");
                textView.append("\nWindspeed: " + String.valueOf(windSpeed) + "m/s");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Given a URL, establishes an HttpUrlConnection and retrieves
         * the web page content as a InputStream, which it returns as a string.
         */

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 1000;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                return readIt(is, len);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

    }
}




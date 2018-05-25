package tcss450.uw.edu.messengerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.messengerapp.R;
import tcss450.uw.edu.messengerapp.utils.SendPostAsyncTask;

public class MyLocationsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MyLocationsActivity";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 814;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private TextView mLocationTextView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.loadWeather);
        fab.setOnClickListener(this::handleWeather);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        }

        mLocationTextView = (TextView) findViewById(R.id.location_text_view);

    }

    private void handleWeather(View view) {
        Log.e("LAT", String.valueOf(mCurrentLocation.getLatitude()));
        Log.e("long",String.valueOf(mCurrentLocation.getLongitude()));

        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("lat", String.valueOf(mCurrentLocation.getLatitude()));
            messageJson.put("long", String.valueOf(mCurrentLocation.getLongitude()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("getWeather")
                .build()
                .toString();
        Log.e("url",mSendUrl);
        Log.e("long",messageJson.toString());
        try {
            Log.e("lat boi",messageJson.getString("lat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson).onCancelled(this::handleError)
                .onPostExecute(this::endOfSendMsgTask).build().execute();
        TextView a =  (TextView) findViewById(R.id.currentTemp);

    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void endOfSendMsgTask(final String result) {

        Log.e("returned value from weather",String.valueOf(mCurrentLocation.getLongitude()));

        try {
            JSONObject res = new JSONObject(result);
            JSONObject current = res.getJSONObject("current");
            TextView a =  (TextView) findViewById(R.id.currentTemp);
           // a.setText("Current Information Temperature: " + current.getString("temperature").toString() +  " Summary: " + current.getString("summary").toString());
            a.setText("Current Information Temperature: " + current.getString("temperature").toString() +  "Summary: " + current.getString("summary").toString());


            JSONObject hourly = res.getJSONObject("hourly");
            JSONArray hourlyData = hourly.getJSONArray("data");
            String hour = "Hourly Data\n";


            for (int i = 0 ; i < hourlyData.length(); i++) {
                JSONObject msg = hourlyData.getJSONObject(i);
                String temperature = msg.get("temperature").toString();
                String summary = msg.get("summary").toString();


                String data = "Time +" + i + "hours: " + temperature + " " + summary + "\n";
                hour = hour + data;

            }
            TextView b =  (TextView) findViewById(R.id.hourlyData);
            b.setText(hour);


            JSONObject daily = res.getJSONObject("daily");
            JSONArray dailyData = daily.getJSONArray("data");
            String day = "Daily Data\n";


            for (int i = 0 ; i < dailyData.length(); i++) {
                JSONObject msg = dailyData.getJSONObject(i);
                String temperature = msg.get("temperature").toString();
                String summary = msg.get("summary").toString();


                String data = "Day +" + i + ": " + temperature + " " + summary + "\n";
                day = day + data;

            }
            TextView c =  (TextView) findViewById(R.id.dailyData);
            c.setText(day);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down…maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_my_locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    @SuppressWarnings("deprecation")
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        //(http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    @SuppressWarnings("deprecation")
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        //(http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    @Override
    @SuppressWarnings("deprecation")
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        if (mCurrentLocation == null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
                    Log.i(TAG, mCurrentLocation.toString());
                }
                startLocationUpdates();
            }
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, mCurrentLocation.toString());
        mLocationTextView.setText(mCurrentLocation.getLatitude() + " " +
                mCurrentLocation.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }
}

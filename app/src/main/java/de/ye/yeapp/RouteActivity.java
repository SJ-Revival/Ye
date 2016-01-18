package de.ye.yeapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import de.ye.yeapp.R;
//import com.qualcomm.vuforia.samples.VuforiaSamples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.ye.yeapp.data.Route;
import de.ye.yeapp.data.User;
import de.ye.yeapp.utils.Connection;
import de.ye.yeapp.utils.GeoFactory;
import de.ye.yeapp.utils.JsonParser;
import de.ye.yeapp.data.Address;
import de.ye.yeapp.utils.MyApplication;
import de.ye.yeapp.utils.RouteFactory;
import de.ye.yeapp.utils.StationFactory;
import de.ye.yeapp.utils.TripFactory;

/**
 * Created by bianca on 30.11.15.
 */
public class RouteActivity extends Activity implements LocationListener {
    private static final String TAG = RouteActivity.class.getSimpleName();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public static final String GOOGLE_GEOCODER = "https://maps.googleapis.com/maps/api/geocode/json?&latlng=";
    public static final String GOOGLE_DIRECTION = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String VBB_DIRECTION = "http://demo.hafas.de/openapi/vbb-proxy/trip?";
    public static final String JSON_OBJECT = "results";

    private String startStop = "Osloer+Strasse";
    private String endStop = "Alexanderplatz";

    private Context context;

    private Address userAddress;
    private User user;

    private List<Route> listRoute;
    private MyApplication myApplication;


    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getApplicationContext();
        //Singleton Objekt um zwischen Activities untereinander Daten auszutauschen
        myApplication = (MyApplication) this.getApplicationContext();
        //ermittelt aktuelle Position des Users
        //userAddress = getLocation();

        listRoute = new ArrayList<Route>();

        //if(userAddress.getCity() != "EMPTY"){
            //Ermittelt eine Route zwischen zwei Positionen
            new AsyncTaskGetVbbDirection().execute();
        //}


        user = new User();
        //user.setAddress(userAddress);
        //User-Daten in der Singleton fuer andere Activities zur Verfuegung stellen
        myApplication.setUser(user);

        //Toast.makeText(this, ""+" "+userAddress.getCity(), Toast.LENGTH_LONG).show();
    }

    //ermittelt die aktuelle Position des Users
    public Address getLocation() {
        Log.d(TAG, "getLocation");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //userLocation =  new LocationClient(this, this, this);

        Location location = null;

        try {

            //  GPS status
            Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Netzwerk status
            Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // Netzwerk und GPS nicht aktiviert
                //Log.d(TAG, "get Location: kein netzwerk und kein gps");
            } else {
                //this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //Log.d(TAG, "isNetworkEnabled = TRUE");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);


                    if (locationManager != null) {
                        //Log.d(TAG, "LocationManager");

                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.

                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //Log.d(TAG, "Location");
                            Log.d(TAG, location.getLatitude()+" "+location.getLongitude());
                            userAddress = new Address();
                            userAddress.setLat(location.getLatitude());
                            userAddress.setLon(location.getLongitude());
                        }else{
                            //Log.d(TAG, "keine Locationr");
                        }
                    }else{//Log.d(TAG, "kein LocationManager");
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    //Log.d(TAG, "isGPSEnabled = TRUE");
                    if (location == null) {
                        //Log.d(TAG, "kein Location");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            //Log.d(TAG, " LocationManager");
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                //Log.d(TAG, "Location");
                                userAddress = new Address();
                                userAddress.setLat(location.getLatitude());
                                userAddress.setLon(location.getLongitude());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if(location != null){
            new AsyncTaskGetGeo().execute();
        }else{
            userAddress = new Address();
            userAddress.setCity("EMPTY");

        }
        return userAddress;
    }

    private int checkSelfPermission(String accessFineLocation) {
        return 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        userAddress.setLat(location.getLatitude());
        userAddress.setLon(location.getLongitude());
        user.setAddress(userAddress);
        new AsyncTaskGetGeo().execute();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // Umwandeln der Geo Daten in eine Adresse
    public class AsyncTaskGetGeo extends AsyncTask<String, String,String> {

        @Override
        protected String doInBackground(String... strings) {

            JsonParser parser = new JsonParser();
            JSONObject jsonObject = parser.getJSONObjectFromUrl(GOOGLE_GEOCODER + userAddress.getLat() + "," + userAddress.getLon() + "&sensor=true", new HashMap<String, String>(), "GET");

            try {
                String status = jsonObject.getString("status");
                if(status.equals("OK")){

                    JSONArray jsonArray = jsonObject.getJSONArray(JSON_OBJECT);

                    //for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObjectLocation = jsonArray.getJSONObject(0);
                    Log.d(TAG,"jsonObjectLocation: "+jsonObjectLocation.toString());

                    JSONArray jsonArrayAddressComponents = jsonObjectLocation.getJSONArray("address_components");

                    for(int i = 0; i < jsonArrayAddressComponents.length(); i++){
                        JSONObject jsonAddress = jsonArrayAddressComponents.getJSONObject(i);
                        Log.d(TAG,"jsonaddress: "+jsonAddress.toString());
                        userAddress = GeoFactory.createAddress(jsonAddress);
                    }

                    //user.setAddress(userAddress);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //new AsyncTaskUserData().execute();
        }
    }

    // Umwandeln der Geo Daten in eine Adresse
    /*public class AsyncTaskGetDirection extends AsyncTask<String, String,String> {

        @Override
        protected String doInBackground(String... strings) {

            JsonParser parser = new JsonParser();

            String googleRequest = GOOGLE_DIRECTION;
            googleRequest += "origin="+startStop;
            googleRequest += "&destination="+endStop;
            googleRequest += "&key="+getResources().getString(R.string.google_browser_api);
            googleRequest += "&mode=transit";
            googleRequest += "&transit_mode=subway";

            Log.d(TAG, "google request: "+googleRequest);

            JSONObject jsonObject = parser.getJSONObjectFromUrl(googleRequest, new HashMap<String, String>(), "GET");

            try {
                String status = jsonObject.getString("status");
                if(status.equals("OK")){

                    JSONArray jsonArray = jsonObject.getJSONArray("routes");

                    //ist eine Array, da google mehrere Routen zurueckliefern kann
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectLocation = jsonArray.getJSONObject(i);
                        Route route = RouteFactory.createRoute(jsonObjectLocation);
                        listRoute.add(route);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myApplication.setRoute(listRoute);
        }
    }*/

    public class AsyncTaskGetVbbDirection extends AsyncTask<String, String,String> {

        @Override
        protected String doInBackground(String... strings) {

            JsonParser parser = new JsonParser();

            String vbbRequest = VBB_DIRECTION;

            //Leo
            vbbRequest += "originExtId="+"9009102";
            //Alex
            vbbRequest += "&destExtId="+"009100003";
            vbbRequest += "&accessId="+getResources().getString(R.string.vbb_api);
            vbbRequest += "&format=json";

            Log.d(TAG, "google request: "+vbbRequest);

            if(Connection.isInternetAvailable()) {
                JSONObject jsonObject = parser.getJSONObjectFromUrl(vbbRequest, new HashMap<String, String>(), "GET");
                if (jsonObject != null) {
                    try {

                        if (jsonObject.has("errorCode")) {
                            /*@todo*/

                        } else {
                            JSONArray jsonArray = jsonObject.getJSONArray("Trip");

                            //ist eine Array, da google mehrere Routen zurueckliefern kann
                            //for(int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < 1; i++) {
                                    JSONObject jsonObjectLocation = jsonArray.getJSONObject(i);
                                    Route route = TripFactory.createTrip(jsonObjectLocation);
                                    listRoute.add(route);
                                }
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myApplication.setRoute(listRoute);
        }
    }


}

package de.ye.yeapp.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import de.ye.yeapp.data.Position;
import de.ye.yeapp.data.RouteStep;
import de.ye.yeapp.data.Stop;
import de.ye.yeapp.data.TransitDetails;

/**
 * Created by bianca on 30.11.15.
 */
public class TransitDetailsFactory {

    private static final String TAG = TransitDetailsFactory.class.getSimpleName();

    public TransitDetailsFactory() {

    }

    public static TransitDetails createTransitDetail(JSONObject jsonObject){
        Log.d(TAG, "createTransitDetail()");
        TransitDetails transitDetails = new TransitDetails();

        Iterator<?> keys = jsonObject.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            Object value = null;
            try {
                value = jsonObject.get(key);

                if(value instanceof String) {
                    Log.d(TAG, "createTransitDetail() String: "+key);
                    String stringValue = (String)value;
                    if(key.equals("line_short_name")) {
                        transitDetails.setLineShortName(stringValue);
                    }else if(key.equals("headsign")) {
                        transitDetails.setHeadsign(stringValue);
                    }

                    //Log.d(TAG, "Key:  " + key + " Value String: " + value);
                }else if(value instanceof Boolean) {
                    Log.d(TAG, "createTransitDetail() Boolean: "+key);
                    Boolean booleanValue = (Boolean)value;
                    /*if(key.equals("isOpen")){
                        location.setIsOpen(booleanValue);
                    }*/
                }else if(value instanceof Integer) {
                    Log.d(TAG, "createTransitDetail() Integer: "+key);
                    Integer intValue = (Integer)value;

                }else if(value instanceof Double) {
                    Log.d(TAG, "createTransitDetail() Double: "+key);
                    //Double doubleValue = (Double)value;

                }else if(value instanceof JSONArray) {
                    Log.d(TAG, "createTransitDetail() Array: "+key);
                    JSONArray listValue = (JSONArray)value;



                }else if(value instanceof  JSONObject){
                    Log.d(TAG, "createTransitDetail() Object: "+key);
                    JSONObject object = (JSONObject)value;

                    if(key.equals("line")) {
                        transitDetails.setLineType(object.getJSONObject("vehicle").getString("type"));
                        transitDetails.setLineShortName(object.getString("short_name"));
                    }else if(key.equals("arrival_time")) {
                        transitDetails.setArrivalTime(object.getString("text"));

                    }else if(key.equals("departure_stop")) {
                        Position pos = new Position();
                        pos.setLat((float) object.getJSONObject("location").getDouble("lat"));
                        pos.setLon((float) object.getJSONObject("location").getDouble("lng"));

                        Stop stop = new Stop();
                        stop.setPosition(pos);
                        stop.setName(object.getString("name"));
                        transitDetails.setDepartureStop(stop);

                    }else if(key.equals("departure_time")) {
                        transitDetails.setDepartureTime(object.getString("text"));

                    }else if(key.equals("arrival_stop")) {
                        Position pos = new Position();
                        pos.setLat((float) object.getJSONObject("location").getDouble("lat"));
                        pos.setLon((float) object.getJSONObject("location").getDouble("lng"));

                        Stop stop = new Stop();
                        stop.setPosition(pos);
                        stop.setName(object.getString("name"));
                        transitDetails.setArrivalStop(stop);

                    }
                }
                else{
                    Log.d(TAG, "createTransitDetail() OTHER: " + key + " Type: " + value.getClass().getSimpleName());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return transitDetails;
    }
}

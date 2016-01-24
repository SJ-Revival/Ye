package de.ye.app.utils;

import android.util.Log;
import de.ye.app.data.Station;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by bianca on 23.11.15.
 */
public class StationFactory {


    private static final String TAG = StationFactory.class.getSimpleName();

    public StationFactory() {

    }

    public static Station createStation(JSONObject jsonObject) {
        Log.d(TAG, "createStation()");
        Station station = new Station();

        Iterator<?> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = null;
            try {
                value = jsonObject.get(key);

                if (value instanceof String) {
                    Log.d(TAG, "createStation() String: " + key);
                    String stringValue = (String) value;
                    if (key.equals("name")) {
                        station.setName(stringValue);
                    } else if (key.equals("id")) {
                        station.setId(stringValue);
                    } else if (key.equals("type")) {
                        station.setType(stringValue);
                    }

                    //Log.d(TAG, "Key:  " + key + " Value String: " + value);
                } else if (value instanceof Boolean) {
                    //Log.d(TAG, "createStation() Boolean: "+key);
                    Boolean booleanValue = (Boolean) value;
                    /*if(key.equals("isOpen")){
                        location.setIsOpen(booleanValue);
                    }*/
                } else if (value instanceof Integer) {
                    //Log.d(TAG, "createStation() Integer: "+key);
                    Integer intValue = (Integer) value;

                } else if (value instanceof Double) {
                    //Log.d(TAG, "createStation() Double: "+key);
                    //Double doubleValue = (Double)value;

                } else if (value instanceof JSONArray) {
                    //Log.d(TAG, "createStation() Array: "+key);
                    JSONArray listValue = (JSONArray) value;


                } else {
                    Log.d(TAG, "createStation() OTHER: " + key + " Type: " + value.getClass().getSimpleName());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return station;
    }
}
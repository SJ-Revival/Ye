package de.ye.app.utils;

import android.util.Log;
import de.ye.app.data.Route;
import de.ye.app.data.RouteStep;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bianca on 12.01.16.
 */
public class TripFactory {

    private static final String TAG = StepFactory.class.getSimpleName();

    public TripFactory() {

    }

    public static Route createTrip(JSONObject jsonObject) {
        Log.d(TAG, "createTrip()");
        Route route = new Route();

        //step.setStops( getStops() );

        Iterator<?> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = null;
            try {
                value = jsonObject.get(key);

                if (value instanceof String) {
                    Log.d(TAG, "createStep() String: " + key);
                    String stringValue = (String) value;
                    if (key.equals("duration")) {
                        route.setDuration(stringValue);
                    } else if (key.equals("tripId")) {
                        route.setTripId(stringValue);
                    }

                    //Log.d(TAG, "Key:  " + key + " Value String: " + value);
                } else if (value instanceof Boolean) {
                    Log.d(TAG, "createTrip() Boolean: " + key);
                    Boolean booleanValue = (Boolean) value;
                    /*if(key.equals("isOpen")){
                        location.setIsOpen(booleanValue);
                    }*/
                } else if (value instanceof Integer) {
                    Log.d(TAG, "createTrip() Integer: " + key);
                    Integer intValue = (Integer) value;

                } else if (value instanceof Double) {
                    Log.d(TAG, "createTrip() Double: " + key);
                    //Double doubleValue = (Double)value;

                } else if (value instanceof JSONArray) {
                    Log.d(TAG, "createTrip() Array: " + key);
                    JSONArray listValue = (JSONArray) value;


                } else if (value instanceof JSONObject) {
                    Log.d(TAG, "createTrip() Object: " + key);
                    JSONObject object = (JSONObject) value;

                    if (key.equals("LegList")) {
                        //Log.d(TAG, "createTrip() Object: "+key + " distance "+object.getString("text"));
                        //step.setDistance(object.getString("text"));

                        JSONArray leglist = object.getJSONArray("Leg");

                        List<RouteStep> steps = new ArrayList<RouteStep>();
                        for (int i = 0; i < leglist.length(); i++) {
                            JSONObject stepObject = (JSONObject) leglist.get(i);
                            RouteStep step = StepFactory.createStep(stepObject);
                            steps.add(step);
                        }

                        route.setSteps(steps);

                    }

                } else {
                    Log.d(TAG, "createTrip() OTHER: " + key + " Type: " + value.getClass().getSimpleName());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return route;
    }


}

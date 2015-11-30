package de.ye.yeapp.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ye.yeapp.data.Position;
import de.ye.yeapp.data.RouteStep;
import de.ye.yeapp.data.Station;
import de.ye.yeapp.data.Stop;
import de.ye.yeapp.data.TransitDetails;

/**
 * Created by bianca on 30.11.15.
 */
public class StepFactory {

    private static final String TAG = StepFactory.class.getSimpleName();

    public StepFactory() {

    }

    public static RouteStep createStep(JSONObject jsonObject){
        Log.d(TAG, "createStep()");
        RouteStep step = new RouteStep();

        Iterator<?> keys = jsonObject.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            Object value = null;
            try {
                value = jsonObject.get(key);

                if(value instanceof String) {
                    Log.d(TAG, "createStep() String: "+key);
                    String stringValue = (String)value;
                    if(key.equals("html_instructions")) {
                        step.setInstruction(stringValue);
                    }else if(key.equals("travel_mode")) {
                        step.setTravelMode(stringValue);
                    }

                    //Log.d(TAG, "Key:  " + key + " Value String: " + value);
                }else if(value instanceof Boolean) {
                    Log.d(TAG, "createStep() Boolean: "+key);
                    Boolean booleanValue = (Boolean)value;
                    /*if(key.equals("isOpen")){
                        location.setIsOpen(booleanValue);
                    }*/
                }else if(value instanceof Integer) {
                    Log.d(TAG, "createStep() Integer: "+key);
                    Integer intValue = (Integer)value;

                }else if(value instanceof Double) {
                    Log.d(TAG, "createStep() Double: "+key);
                    //Double doubleValue = (Double)value;

                }else if(value instanceof JSONArray) {
                    Log.d(TAG, "createStep() Array: "+key);
                    JSONArray listValue = (JSONArray)value;

                    if(key.equals("steps")) {
                        List<RouteStep> routeSteps = new ArrayList<RouteStep>();

                        for (int i = 0; i < listValue.length(); i++) {
                            JSONObject stepObject = (JSONObject) listValue.get(i);
                            RouteStep stepitem = StepFactory.createStep(stepObject);
                            routeSteps.add(stepitem);

                        }

                        step.setSteps(routeSteps);
                    }

                }else if(value instanceof  JSONObject){
                    Log.d(TAG, "createStep() Object: "+key);
                    JSONObject object = (JSONObject)value;

                    if(key.equals("distance")) {
                        //Log.d(TAG, "createStep() Object: "+key + " distance "+object.getString("text"));
                        step.setDistance(object.getString("text"));
                    }else if(key.equals("duration")) {
                        step.setDuration(object.getString("text"));
                    }else if(key.equals("end_location")) {
                        Position pos = new Position();
                        pos.setLat((float) object.getDouble("lat"));
                        pos.setLon((float) object.getDouble("lng"));
                        step.setEndLocation(pos);

                    }else if(key.equals("transit_details")) {
                        Log.d(TAG, "createStep() Array: legs steps transit_details");

                        TransitDetails transitDetails = TransitDetailsFactory.createTransitDetail(object);
                        step.setTransitDetails(transitDetails);
                    }else if(key.equals("start_location")) {

                        Position pos = new Position();
                        pos.setLat((float) object.getDouble("lat"));
                        pos.setLon((float) object.getDouble("lng"));
                        step.setEndLocation(pos);
                    }

                }
                else{
                    Log.d(TAG, "createStep() OTHER: "+key+" Type: "+value.getClass().getSimpleName());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return step;
    }
}

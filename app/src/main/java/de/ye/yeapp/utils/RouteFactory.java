package de.ye.yeapp.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ye.yeapp.data.Position;
import de.ye.yeapp.data.Route;
import de.ye.yeapp.data.RouteStep;
import de.ye.yeapp.data.Station;

/**
 * Created by bianca on 30.11.15.
 */
public class RouteFactory {

    private static final String TAG = RouteFactory.class.getSimpleName();

    public RouteFactory() {

    }

    public static Route createRoute(JSONObject jsonObject){
        Log.d(TAG, "createRoute()");
        Route route = new Route();

        Iterator<?> keys = jsonObject.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            Object value = null;
            try {
                value = jsonObject.get(key);

                if(value instanceof String) {
                    Log.d(TAG, "createRoute() String: "+key);
                    String stringValue = (String)value;
                    /*if(key.equals("arrival_time")) {
                        route.setArrivalTime(stringValue);
                    }*/
                    //Log.d(TAG, "Key:  " + key + " Value String: " + value);
                }else if(value instanceof Boolean) {
                    Log.d(TAG, "createRoute() Boolean: "+key);
                    Boolean booleanValue = (Boolean)value;
                    /*if(key.equals("isOpen")){
                        location.setIsOpen(booleanValue);
                    }*/
                }else if(value instanceof Integer) {
                    Log.d(TAG, "createRoute() Integer: "+key);
                    Integer intValue = (Integer)value;

                }else if(value instanceof Double) {
                    Log.d(TAG, "createRoute() Double: "+key);
                    Double doubleValue = (Double)value;

                    /*if(key.equals("northeast_lat")){
                        route.setNortheastLat(doubleValue);
                    }else if(key.equals("northeast_lon")){
                        route.setNortheastLat(doubleValue);
                    }else if(key.equals("southwest_lat")){
                        route.setNortheastLat(doubleValue);
                    }else if(key.equals("southwest_lon")){
                        route.setNortheastLat(doubleValue);
                    }*/

                }else if(value instanceof JSONArray) {
                    Log.d(TAG, "createRoute() Array: "+key);
                    JSONArray listValue = (JSONArray)value;

                    if(key.equals("legs")){

                        if(listValue.length() > 0){
                            JSONObject object = (JSONObject) listValue.get(0);

                            // STRINGS
                            /*if(object.has("arrival_time")){
                                Log.d(TAG, "createroute() Array: legs arrival_time "+object.getJSONObject("arrival_time").getString("text"));
                                route.setArrivalTime(object.getJSONObject("arrival_time").getString("text"));
                            }
                            if(object.has("departure_time")){
                                Log.d(TAG, "createroute() Array: legs departure_time");
                                route.setDepartureTime(object.getJSONObject("departure_time").getString("text"));
                            }
                            if(object.has("distance")){
                                Log.d(TAG, "createroute() Array: legs distance");
                                route.setDistance(object.getJSONObject("distance").getString("text"));
                            }
                            if(object.has("duration")){
                                Log.d(TAG, "createroute() Array: legs duration");
                                route.setDuration(object.getJSONObject("duration").getString("text"));
                            }
                            if(object.has("end_address")){
                                Log.d(TAG, "createroute() Array: legs end_address");
                                route.setEndAddress(object.getString("end_address"));
                            }
                            if(object.has("start_address")){
                                Log.d(TAG, "createroute() Array: legs start_address");
                                route.setStartAddress(object.getString("start_address"));
                            }

                            // POSITIONS
                            if(object.has("end_location")){
                                Log.d(TAG, "createroute() Array: legs end_location");
                                JSONObject ob = object.getJSONObject("end_location");
                                Position pos = new Position();
                                pos.setLat((float) ob.getDouble("lat"));
                                pos.setLon((float) ob.getDouble("lng"));
                                route.setStartLocation(pos);
                            }
                            if(object.has("start_location")){
                                Log.d(TAG, "createroute() Array: legs start_location");
                                JSONObject ob = object.getJSONObject("start_location");
                                Position pos = new Position();
                                pos.setLat((float) ob.getDouble("lat"));
                                pos.setLon((float) ob.getDouble("lng"));
                                route.setStartLocation(pos);
                            }*/

                            //STEPS
                            if(object.has("steps")){
                                Log.d(TAG, "createroute() Array: legs steps");
                                JSONArray listSteps = object.getJSONArray("steps");
                                Log.d(TAG, "createroute() Array: legs steps length "+listSteps.length());
                                List<RouteStep> routeSteps = new ArrayList<RouteStep>();
                                for(int i = 0; i < listSteps.length(); i++){
                                    JSONObject stepObject = (JSONObject) listSteps.get(i);
                                    RouteStep step = StepFactory.createStep(stepObject);
                                    routeSteps.add(step);
                                }
                                route.setSteps(routeSteps);
                            }
                        }
                    }


                }else if(value instanceof JSONObject){
                    Log.d(TAG, "createroute() Object: "+key);
                    JSONObject object = (JSONObject)value;

                }
                else{
                    Log.d(TAG, "createroute() OTHER: "+key+" Type: "+value.getClass().getSimpleName());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return route;
    }
}

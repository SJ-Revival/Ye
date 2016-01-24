package de.ye.app.utils;

import de.ye.app.data.Address;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by bianca on 30.11.15.
 */
public class GeoFactory {

    private static final String TAG = GeoFactory.class.getSimpleName();

    public GeoFactory() {
    }

    public static Address createAddress(JSONObject jsonObject) {

        Address address = new Address();

        Iterator<?> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            JSONArray typeArray;
            String typeString;
            Object value = null;
            if (key.equals("long_name")) {
                try {
                    value = jsonObject.get(key);
                    typeArray = jsonObject.getJSONArray("types");
                    typeString = (String) typeArray.get(0);

                    if (value instanceof String) {

                        String stringValue = (String) value;

                        //Log.d(TAG, typeString + ": " + stringValue);
                        if (typeString.equals("street_number")) {
                            address.setNumber(stringValue);
                        } else if (typeString.equals("route")) {
                            address.setStreet(stringValue);
                        } else if (typeString.equals("locality")) {
                            address.setCity(stringValue);
                        } else if (typeString.equals("postal_code")) {
                            address.setZip(stringValue);
                        } else if (typeString.equals("country")) {
                            address.setCountry(stringValue);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return address;

    }
}

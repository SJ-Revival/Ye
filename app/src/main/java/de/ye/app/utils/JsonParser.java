package de.ye.app.utils;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Map;

/**
 * Created by bianca on 10.07.15.
 */
public class JsonParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArr = null;
    static String json = "";
    private static final String TAG = JsonParser.class.getSimpleName();

    public JSONArray getJSONArrayFromUrl(String request_url, Map<String, String> map, String type) {
        Log.d(TAG, "getJSONArrayFromUrl() " + request_url);
        // versuche Request
        String json = readJson(request_url, map, type);

        // try parse the string to a JSON Array
        try {
            //jArr = new JSONArray(json);
            jArr = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }
        return jArr;
    }


    public JSONObject getJSONObjectFromUrl(String request_url, Map<String, String> map, String type) {
        Log.d(TAG, "getJSONObjectFromUrl() " + request_url);
        // versuche Request
        String json = readJson(request_url, map, type);

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return jObj;

    }


    private String readJson(String request_url, Map<String, String> map, String type) {
        try {

            //Log.d(TAG, "readJson() TRY");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //Log.d(TAG, "readJson() TRY httpClient ");

            HttpResponse httpResponse;

            HttpGet httpRequest = new HttpGet(request_url);
            httpResponse = httpClient.execute(httpRequest);

            int code = httpResponse.getStatusLine().getStatusCode();
            //Log.d(TAG, "getJSONFromUrl() TRY Int: "+code);

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {

                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            //Log.d(TAG, "JSON: "+json);

        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        return json;


    }

}
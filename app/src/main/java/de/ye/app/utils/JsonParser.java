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
 * Some JSON utility functions
 */
public class JsonParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArr = null;
    static String json = "";
    private static final String LOGTAG = JsonParser.class.getSimpleName();

    public JSONArray getJSONArrayFromUrl(String request_url, Map<String, String> map, String type) {
        Log.d(LOGTAG, "getJSONArrayFromUrl() " + request_url);
        // versuche Request
        String json = readJson(request_url, map, type);

        // try parse the string to a JSON Array
        try {
            //jArr = new JSONArray(json);
            jArr = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }
        return jArr;
    }


    public JSONObject getJSONObjectFromUrl(String request_url, Map<String, String> map, String type) {
        Log.d(LOGTAG, "getJSONObjectFromUrl() " + request_url);
        // versuche Request
        String json = readJson(request_url, map, type);

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }

        return jObj;

    }


    private String readJson(String request_url, Map<String, String> map, String type) {
        try {

            //Log.d(LOGTAG, "readJson() TRY");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //Log.d(LOGTAG, "readJson() TRY httpClient ");

            HttpResponse httpResponse;

            HttpGet httpRequest = new HttpGet(request_url);
            httpResponse = httpClient.execute(httpRequest);

            int code = httpResponse.getStatusLine().getStatusCode();
            //Log.d(LOGTAG, "getJSONFromUrl() TRY Int: "+code);

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
            String line;
            while ((line = reader.readLine()) != null) {
                line += "\n";
                sb.append(line);
            }
            is.close();
            json = sb.toString();
            //Log.d(LOGTAG, "JSON: "+json);

        } catch (Exception e) {
            Log.e(LOGTAG, "Error converting result " + e.toString());
        }

        return json;
    }

    public JSONObject readJson(InputStream is) {
        String json;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            int chars = is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d(LOGTAG, "Read " + chars + " characters of JSON.");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "ERROR creating JSON");
            e.printStackTrace();
            return null;
        }
    }
}

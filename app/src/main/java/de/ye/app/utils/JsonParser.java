package de.ye.app.utils;

import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Some JSON utility functions
 */
public class JsonParser {
    private static final String LOGTAG = JsonParser.class.getSimpleName();
    static InputStream is = null;
    static JSONObject jsonObject = null;
    static JSONArray jsonArray = null;
    static String jsonString = "";

    public JSONArray getJSONArrayFromUrl(String request_url, Map<String, String> map, String type) {
        Log.d(LOGTAG, "getJSONArrayFromUrl() " + request_url);
        String json = readJsonString(request_url, map, type);

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }
        return jsonArray;
    }

    public JSONArray getJSONArrayFromUrl(String request_url, String username, String password) {
        Log.d(LOGTAG, "getJSONArrayFromUrl() " + request_url);
        String json = readJsonString(request_url, username, password);

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }
        return jsonArray;
    }

    public JSONArray getJSONArrayFromUrl(String request_url) {
        Log.d(LOGTAG, "getJSONArrayFromUrl() " + request_url);
        String json = readJsonString(request_url);

        if (json != null) {
            Log.i(LOGTAG, "json has some values");
            // try to parse the string into a JSONArray
            try {
                jsonArray = new JSONArray(json);
            } catch (JSONException e) {
                Log.e(LOGTAG, "Error parsing data " + e.toString());
            }
        } else {
            Log.i(LOGTAG, "json does not have values");
        }

        return jsonArray;
    }

    public JSONObject getJSONObjectFromUrl(String request_url) {
        Log.d(LOGTAG, "getJSONObjectFromUrl() " + request_url);
        String json = readJsonString(request_url);

        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }

        return jsonObject;
    }

    public JSONObject getJSONObjectFromUrl(String request_url, String username, String password) {
        Log.d(LOGTAG, "getJSONObjectFromUrl() " + request_url);
        String json = readJsonString(request_url, username, password);

        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Error parsing data " + e.toString());
        }

        return jsonObject;
    }

    public JSONObject getJSONObjectFromUrl(String request_url, Map<String, String> map, String type) {
        return getJSONObjectFromUrl(request_url);
    }

    private String readJsonString(String request_url, Map<String, String> map, String type) {
        return readJsonString(request_url);
    }

    private String readJsonString(String request_url, String username, String password) {

        final int BUFFER_SIZE = 1024;
        BufferedReader reader = null;

        try {
            URL url = new URL(request_url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            if (!username.equals("") && !password.equals("")) {
                String userpass = username + ":" + password;
                String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes(), Base64.DEFAULT));
                httpConn.setRequestProperty("Authorization", basicAuth);
            }

            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
//                String disposition = httpConn.getHeaderField("Content-Disposition");
//                String contentType = httpConn.getContentType();
//                int contentLength = httpConn.getContentLength();

//                Log.d(LOGTAG, "Content-Type = " + contentType);
//                Log.d(LOGTAG, "Content-Disposition = " + disposition);
//                Log.d(LOGTAG, "Content-Length = " + contentLength);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // opens an output stream to save into file
                StringBuilder sb = new StringBuilder();

                int charsRead;
                char[] chars = new char[BUFFER_SIZE];
                while ((charsRead = reader.read(chars)) != -1) {
                    sb.append(chars, 0, charsRead);
                }
                inputStream.close();
                return sb.toString();
            } else {
                Log.e(LOGTAG, "No file found. Server replied with HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        BufferedReader reader = null;
//
//        try {
//            URL url = new URL(request_url);
//            reader = new BufferedReader(new InputStreamReader(url.openStream()));
//            StringBuilder buffer = new StringBuilder();
//            int read;
//            char[] chars = new char[1024];
//            while ((read = reader.read(chars)) != -1) {
//                buffer.append(chars, 0, read);
//            }
//            jsonString = buffer.toString();
//            return jsonString;
//        } catch (Exception e) {
//            Log.e(LOGTAG, "Error converting result " + e.toString());
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return null;
    }

    private String readJsonString(String request_url) {
        return readJsonString(request_url, "", "");
    }

    public JSONObject readJsonObject(InputStream is) {
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

    public JSONArray readJsonArray(InputStream is) {
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
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e(LOGTAG, "ERROR creating JSON");
            e.printStackTrace();
            return null;
        }
    }
}

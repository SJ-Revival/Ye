package de.ye.yeapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by bianca on 12.01.16.
 */
public class FileReader {

    private static final String TAG = FileReader.class.getSimpleName();

    public static String readFile(Context context, String filePath) throws Exception {
        String returnString = "";

        AssetManager assManager = context.getAssets();
        /*File file = new File(filePath);
        FileInputStream fileIS = new FileInputStream(file);*/
        InputStream fileIS = assManager.open(filePath);
        returnString = streamToString(fileIS);
        fileIS.close();


        return returnString;
    }

    private static String streamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            Log.d(TAG, "line: " + line);
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}

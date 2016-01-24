package de.ye.app.objects;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Shaders {

    private static final String LOGTAG = Shaders.class.getSimpleName();

    // private static AssetManager assets = getAssets();

    public static String loadShaderFromApk(String fileName, AssetManager assets) {
        InputStream inputStream;


        try {
            inputStream = assets.open(fileName, AssetManager.ACCESS_BUFFER);

            BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);

            byte[] contents = new byte[1024];

            int bytesRead;
            String strFileContents = "";

            while ((bytesRead = bufferedStream.read(contents)) != -1) {
                strFileContents = new String(contents, 0, bytesRead);
            }

            return strFileContents;

        } catch (IOException e) {
            Log.e(LOGTAG, "Failed to log texture '" + fileName + "' from APK");
            Log.i(LOGTAG, e.getMessage());
            return null;
        }
    }
}

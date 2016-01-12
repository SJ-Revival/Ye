package de.ye.yeapp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import de.ye.yeapp.utils.FileReader;

/**
 * Created by bianca on 12.01.16.
 */
public class FileReaderExampleActivity extends Activity {

    private static final String TAG = FileReaderExampleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            String str = FileReader.readFile(getApplicationContext(), "YeShader/cube_mesh.fs.glsl");
            Log.d(TAG, "FileReader: "+str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

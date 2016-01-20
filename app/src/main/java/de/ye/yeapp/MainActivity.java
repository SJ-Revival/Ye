package de.ye.yeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import com.qualcomm.vuforia.samples.VuforiaSamples.R;


/**
 * Created by bianca on 20.11.15.
 */
public class MainActivity extends Activity{


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Button btScan = (Button) findViewById(R.id.btScan);
        btScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ArrivalActivity.class);
            startActivity(intent);
            }
        });

    }
}

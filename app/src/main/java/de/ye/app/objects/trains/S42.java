package de.ye.app.objects.trains;

import de.ye.app.objects.TrainPath;

import java.util.ArrayList;
import java.util.List;

class TargetPoint {
    float x;
    float y;
    String stationName;

    public TargetPoint(float x, float y, String stationName) {
        this.x = x;
        this.y = y;
        this.stationName = stationName;
    }
}

public class S42 extends TrainPath {

    private final List<TargetPoint> points = new ArrayList<TargetPoint>() {{
        add(new TargetPoint(0f, 45.664f, "Gesundbrunnen"));
        add(new TargetPoint(27.125f, 45.664f, "Schoenhauser Allee"));
        add(new TargetPoint(56.750f, 45.664f, "Prenzlauer Allee"));
        add(new TargetPoint(64.500f, 45.664f, null));
        add(new TargetPoint(70.313f, 39.852f, "Greifswalder Strasse"));
        add(new TargetPoint(84.313f, 25.851f, null));
        add(new TargetPoint(84.313f, 18.414f, "Landsberger Allee"));
        add(new TargetPoint(84.313f, 5.789f, "Storkower Strasse"));
        add(new TargetPoint(84.313f, -6.461f, "Frankfurter Allee"));
        add(new TargetPoint(84.313f, -15.461f, "Ostkreuz"));
        add(new TargetPoint(84.313f, -33.711f, "Treptower Park"));
        add(new TargetPoint(84.313f, -38.961f, null));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
        add(new TargetPoint(0f, 0f, ""));
    }};

    public S42(String trainLineName, ArrayList<double[]> trainCorners, int[] trainStationIndices, ArrayList<String> trainStationNames, Boolean ring) {
        super(trainLineName, trainCorners, trainStationIndices, trainStationNames, ring);
    }
}

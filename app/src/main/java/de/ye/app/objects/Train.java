package de.ye.app.objects;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO maybe Train should extend TrainLine?
public class Train{

    private static final String LOGTAG = Train.class.getSimpleName();

    public Quad quad;

    private String trainLineName;
    private String trainID;
    private int previousStopID;
    private int nextStopID;
    private JSONArray futureProgress;
    private double progress;

    public Train(String trainLineName,
                 String trainID,
                 int previousStopID,
                 int nextStopID,
                 JSONArray futureProgress,
                 double progress) {
        this.quad = new Quad();
        this.trainLineName = trainLineName;
        this.trainID = trainID;
        this.previousStopID = previousStopID;
        this.nextStopID = nextStopID;
        this.futureProgress = futureProgress;
        this.progress = progress;
    }

    public Train(JSONObject object) {
        this.quad = new Quad();

        try {
            // this.trainLineName = object.getString("trainLine");
            this.trainID = object.getString("trainID");
            this.previousStopID = object.getInt("startID");
            this.nextStopID = object.getInt("stopID");
            this.futureProgress = object.getJSONArray("p");
            this.progress = object.getDouble("percentage") / 100;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.trainLineName = "S42";

        // make sure progress is not higher than 1
        if (this.progress > 1) {
            this.progress = 1;
        }

//        Log.i(LOGTAG, this.toString());
    }

    public JSONArray getFutureProgress() {
        return futureProgress;
    }

    public void setFutureProgress(JSONArray futureProgress) {
        this.futureProgress = futureProgress;
    }

    public String getTrainLineName() {
        return trainLineName;
    }

    public void setTrainLineName(String trainLineName) {
        this.trainLineName = trainLineName;
    }

    public String getTrainID() {
        return trainID;
    }

    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    public int getPreviousStopID() {
        return previousStopID;
    }

    public void setPreviousStopID(int previousStopID) {
        this.previousStopID = previousStopID;
    }

    public int getNextStopID() {
        return nextStopID;
    }

    public void setNextStopID(int nextStopID) {
        this.nextStopID = nextStopID;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainLineName='" + trainLineName + '\'' +
                ", trainID='" + trainID + '\'' +
                ", previousStopID=" + previousStopID +
                ", nextStopID=" + nextStopID +
                ", futureProgress=" + futureProgress +
                ", progress=" + progress +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Train train = (Train) o;

        if (previousStopID != train.previousStopID) return false;
        if (nextStopID != train.nextStopID) return false;
        if (Double.compare(train.progress, progress) != 0) return false;
        if (trainLineName != null ? !trainLineName.equals(train.trainLineName) : train.trainLineName != null)
            return false;
        if (trainID != null ? !trainID.equals(train.trainID) : train.trainID != null) return false;
        return futureProgress != null ? futureProgress.equals(train.futureProgress) : train.futureProgress == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = trainLineName != null ? trainLineName.hashCode() : 0;
        result = 31 * result + (trainID != null ? trainID.hashCode() : 0);
        result = 31 * result + previousStopID;
        result = 31 * result + nextStopID;
        result = 31 * result + (futureProgress != null ? futureProgress.hashCode() : 0);
        temp = Double.doubleToLongBits(progress);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

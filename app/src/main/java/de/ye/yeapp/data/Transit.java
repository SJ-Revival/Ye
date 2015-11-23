package de.ye.yeapp.data;

/**
 * Created by bianca on 23.11.15.
 */
public class Transit {

    private String start;
    private String stop;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    private String type;
}

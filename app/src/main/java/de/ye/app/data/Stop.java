package de.ye.app.data;

/**
 * Created by bianca on 30.11.15.
 */
public class Stop {


    private String id;
    private String name;
    private Position position;

    private String extId;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

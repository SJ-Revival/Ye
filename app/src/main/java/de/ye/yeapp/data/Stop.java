package de.ye.yeapp.data;

/**
 * Created by bianca on 30.11.15.
 */
public class Stop {


    private String id;
    private String name;
    private Position position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

package de.ye.yeapp.data;

/**
 * Created by bianca on 23.11.15.
 */
public interface IStation {

    String id = null;
    String name = null;
    String type = null;


    public String getId();
    public String getName();
    public String getType();

    public void setId(String id);
    public void setName(String name);
    public void setType(String type);
}

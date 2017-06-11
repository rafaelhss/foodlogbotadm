package com.foodlog.web.rest.bot.model;

//import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Chat  implements Serializable {

    /*public enum Type {
        @SerializedName("private")Private, group, supergroup, channel
    }*/

    public Long id;
    public String type;
    public String first_name;
    public String last_name;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

}

package com.foodlog.web.rest.bot.model;

import java.io.Serializable;

public class Update  implements Serializable {

    public Long update_id;
    public Message message;

    public Long getUpdate_id() {
        return update_id;
    }
    public void setUpdate_id(Long update_id) {
        this.update_id = update_id;
    }

    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }

}

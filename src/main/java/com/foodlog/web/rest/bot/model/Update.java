package com.foodlog.web.rest.bot.model;

import java.io.Serializable;
import java.time.Instant;

public class Update  implements Serializable {

    private Long update_id;
    private Message message;
    private Instant updateDateTime;



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

    public Instant getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Instant updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
}

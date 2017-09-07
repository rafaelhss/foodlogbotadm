package com.foodlog.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A BodyLog.
 */
@Entity
@Table(name = "body_log")
public class BodyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Lob
    @Column(name = "photo", nullable = false)
    private byte[] photo;

    @Column(name = "photo_content_type", nullable = false)
    private String photoContentType;

    @NotNull
    @Column(name = "body_log_datetime", nullable = false)
    private Instant bodyLogDatetime;

    @NotNull
    @Column(name = "update_id", nullable = false)
    private Long updateId;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public BodyLog photo(byte[] photo) {
        this.photo = photo;
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public BodyLog photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Instant getBodyLogDatetime() {
        return bodyLogDatetime;
    }

    public BodyLog bodyLogDatetime(Instant bodyLogDatetime) {
        this.bodyLogDatetime = bodyLogDatetime;
        return this;
    }

    public void setBodyLogDatetime(Instant bodyLogDatetime) {
        this.bodyLogDatetime = bodyLogDatetime;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public BodyLog updateId(Long updateId) {
        this.updateId = updateId;
        return this;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public User getUser() {
        return user;
    }

    public BodyLog user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BodyLog bodyLog = (BodyLog) o;
        if (bodyLog.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bodyLog.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BodyLog{" +
            "id=" + getId() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + photoContentType + "'" +
            ", bodyLogDatetime='" + getBodyLogDatetime() + "'" +
            ", updateId='" + getUpdateId() + "'" +
            "}";
    }
}

package com.foodlog.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A MealLog.
 */
@Entity
@Table(name = "meal_log")
public class MealLog implements Serializable {

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
    @Column(name = "meal_date_time", nullable = false)
    private Instant mealDateTime;

    @Column(name = "jhi_comment")
    private String comment;

    @Column(name = "update_id")
    private Long updateId;

    @ManyToOne
    private ScheduledMeal scheduledMeal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public MealLog photo(byte[] photo) {
        this.photo = photo;
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public MealLog photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Instant getMealDateTime() {
        return mealDateTime;
    }

    public MealLog mealDateTime(Instant mealDateTime) {
        this.mealDateTime = mealDateTime;
        return this;
    }

    public void setMealDateTime(Instant mealDateTime) {
        this.mealDateTime = mealDateTime;
    }

    public String getComment() {
        return comment;
    }

    public MealLog comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public MealLog updateId(Long updateId) {
        this.updateId = updateId;
        return this;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public ScheduledMeal getScheduledMeal() {
        return scheduledMeal;
    }

    public MealLog scheduledMeal(ScheduledMeal scheduledMeal) {
        this.scheduledMeal = scheduledMeal;
        return this;
    }

    public void setScheduledMeal(ScheduledMeal scheduledMeal) {
        this.scheduledMeal = scheduledMeal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MealLog mealLog = (MealLog) o;
        if (mealLog.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), mealLog.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MealLog{" +
            "id=" + getId() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + photoContentType + "'" +
            ", mealDateTime='" + getMealDateTime() + "'" +
            ", comment='" + getComment() + "'" +
            ", updateId='" + getUpdateId() + "'" +
            "}";
    }
}

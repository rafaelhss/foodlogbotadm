package com.foodlog.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Weight.
 */
@Entity
@Table(name = "weight")
public class Weight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_value", nullable = false)
    private Float value;

    @NotNull
    @Column(name = "weight_date_time", nullable = false)
    private Instant weightDateTime;

    @Column(name = "jhi_comment")
    private String comment;

    @Column(name = "update_id", unique = true)
    private Long updateId;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getValue() {
        return value;
    }

    public Weight value(Float value) {
        this.value = value;
        return this;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Instant getWeightDateTime() {
        return weightDateTime;
    }

    public Weight weightDateTime(Instant weightDateTime) {
        this.weightDateTime = weightDateTime;
        return this;
    }

    public void setWeightDateTime(Instant weightDateTime) {
        this.weightDateTime = weightDateTime;
    }

    public String getComment() {
        return comment;
    }

    public Weight comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public Weight updateId(Long updateId) {
        this.updateId = updateId;
        return this;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public User getUser() {
        return user;
    }

    public Weight user(User user) {
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
        Weight weight = (Weight) o;
        if (weight.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), weight.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Weight{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", weightDateTime='" + getWeightDateTime() + "'" +
            ", comment='" + getComment() + "'" +
            ", updateId='" + getUpdateId() + "'" +
            "}";
    }
}

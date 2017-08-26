package com.foodlog.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ScheduledMeal.
 */
@Entity
@Table(name = "scheduled_meal")
public class ScheduledMeal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Pattern(regexp = "^((?:[01]\\d|2[0-3]):[0-5]\\d$)")
    @Column(name = "target_time", nullable = false)
    private String targetTime;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ScheduledMeal name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public ScheduledMeal description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetTime() {
        return targetTime;
    }

    public ScheduledMeal targetTime(String targetTime) {
        this.targetTime = targetTime;
        return this;
    }

    public void setTargetTime(String targetTime) {
        this.targetTime = targetTime;
    }

    public User getUser() {
        return user;
    }

    public ScheduledMeal user(User user) {
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
        ScheduledMeal scheduledMeal = (ScheduledMeal) o;
        if (scheduledMeal.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), scheduledMeal.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ScheduledMeal{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", targetTime='" + getTargetTime() + "'" +
            "}";
    }
}

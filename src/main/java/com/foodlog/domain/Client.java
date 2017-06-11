package com.foodlog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private Set<ScheduledMeal> scheduledMeals = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public Client telegramId(Long telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getName() {
        return name;
    }

    public Client name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScheduledMeal> getScheduledMeals() {
        return scheduledMeals;
    }

    public Client scheduledMeals(Set<ScheduledMeal> scheduledMeals) {
        this.scheduledMeals = scheduledMeals;
        return this;
    }

    public Client addScheduledMeal(ScheduledMeal scheduledMeal) {
        this.scheduledMeals.add(scheduledMeal);
        scheduledMeal.setClient(this);
        return this;
    }

    public Client removeScheduledMeal(ScheduledMeal scheduledMeal) {
        this.scheduledMeals.remove(scheduledMeal);
        scheduledMeal.setClient(null);
        return this;
    }

    public void setScheduledMeals(Set<ScheduledMeal> scheduledMeals) {
        this.scheduledMeals = scheduledMeals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        if (client.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), client.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", telegramId='" + getTelegramId() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}

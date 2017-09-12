package com.foodlog.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Jaca.
 */
@Entity
@Table(name = "jaca")
public class Jaca implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jaca_date_time", nullable = false)
    private Instant jacaDateTime;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getJacaDateTime() {
        return jacaDateTime;
    }

    public Jaca jacaDateTime(Instant jacaDateTime) {
        this.jacaDateTime = jacaDateTime;
        return this;
    }

    public void setJacaDateTime(Instant jacaDateTime) {
        this.jacaDateTime = jacaDateTime;
    }

    public User getUser() {
        return user;
    }

    public Jaca user(User user) {
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
        Jaca jaca = (Jaca) o;
        if (jaca.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jaca.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Jaca{" +
            "id=" + getId() +
            ", jacaDateTime='" + getJacaDateTime() + "'" +
            "}";
    }
}

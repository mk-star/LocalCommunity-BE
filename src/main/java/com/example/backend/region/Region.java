package com.example.backend.region;

import com.example.backend.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metropolitan;

    private String district;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors, getters, and setters
    public Region() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetropolitan() {
        return metropolitan;
    }

    public void setMetropolitan(String metropolitan) {
        this.metropolitan = metropolitan;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

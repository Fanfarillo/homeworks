package com.logic.entity;

import javax.persistence.*;

@Entity

@Table
public class Address {
    //attributes
    @Id
    @Column(nullable=false, unique=true, length=64)
    private String name;
    private String city;

    //constructor
    public Address() {}

    //getters & setters
    public String getName() {
        return this.name;
    }

    public void setName(String newName) { this.name = newName; }

    public String getCity() {
        return this.city;
    }

    public void setCity(String newCity) {
        this.city = newCity;
    }

}

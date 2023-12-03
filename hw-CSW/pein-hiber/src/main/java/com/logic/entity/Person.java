package com.logic.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table
public class Person {
    //attributes
    @Id
    @Column(nullable=false, unique=true, length=16)
    private String cf;
    @ManyToOne
    private Address residence;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Address> domiciles;

    //constructor
    public Person() {}

    //getters & setters
    public String getCf() {
        return this.cf;
    }

    public void setCf(String newCf) { this.cf = newCf; }

    public Address getResidence() {
        return this.residence;
    }

    public List<Address> getDomiciles() {
        return this.domiciles;
    }

    public void setResidence(Address newResidence) {
        this.residence = newResidence;
    }

    public void setDomiciles(List<Address> newDomiciles) { this.domiciles = newDomiciles; }

}

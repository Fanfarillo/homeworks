package it.uniroma2.sc.demospringhibernate.entity;

import lombok.Data;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Indirizzo {
    @Id
    @GeneratedValue
    private Long id;

    private String viaENumero;

    private String cap;

    @Embedded
    private Citta citta;

    protected Indirizzo() {

    }

    public Indirizzo(String viaENumero, String cap, Citta citta) {
        this.viaENumero = viaENumero;
        this.cap = cap;
        this.citta = citta; //NB: this.citta in constructor added by Fanfa
    }

    //NB: ALL the following is added by Fanfa
    public Long getId() {
        return this.id;
    }

    public String getViaENumero() {
        return this.viaENumero;
    }

    public String getCap() {
        return this.cap;
    }

    public Citta getCitta() {
        return this.citta;
    }

}

package it.uniroma2.sc.demospringhibernate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Cane {
    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty(value = "nomeCane")
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    private Persona padrone;

    public Persona getPadrone() {
        return padrone;
    }

    protected Cane() {

    }

    public Cane(String nome, Persona padrone) {
        this.nome = nome;
        this.padrone = padrone;
    }

    public Cane(Long id, String nome, Persona padrone) {
        this.id = id;
        this.nome = nome;
        this.padrone = padrone;
    }

    @Override
    public String toString() {
        return "Cane{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", padrone=" + padrone +
                '}';
    }

    //NB: ALL the following is added by Fanfa
    public Long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

}

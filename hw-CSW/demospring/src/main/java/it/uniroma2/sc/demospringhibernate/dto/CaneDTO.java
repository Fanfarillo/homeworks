package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

@Data
public class CaneDTO {
    private long id;
    private String nome;
    private PersonaDTO padrone;

    public CaneDTO(){

    }

    public CaneDTO(long id, String nome, PersonaDTO padrone) {
        this.id = id;
        this.nome = nome;
        this.padrone = padrone;

    }

    public long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public PersonaDTO getPadrone() {
        return this.padrone;
    }

}

package it.uniroma2.sc.demospringhibernate.entity;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("L")
@Data
public class Laurea extends TitoloDiStudio {
    private boolean cicloUnico;

    protected Laurea() {
        super();
    }
    public Laurea(String nomeTitolo, int annoConseguimento, boolean cicloUnico) {
        super(nomeTitolo, annoConseguimento);
        this.cicloUnico = cicloUnico;
    }

    //NB: ALL the following is added by Fanfa
    public boolean isCicloUnico() {
        return cicloUnico;
    }

}

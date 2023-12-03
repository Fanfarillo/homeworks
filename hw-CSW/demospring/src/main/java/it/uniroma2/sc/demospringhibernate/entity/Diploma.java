package it.uniroma2.sc.demospringhibernate.entity;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("D")
@Data
public class Diploma extends TitoloDiStudio {
    private String classeDiploma;
    protected Diploma() {
        super();
    }
    public Diploma(String nomeTitolo, int annoConseguimento, String classeDiploma) {
        super(nomeTitolo, annoConseguimento);
        this.classeDiploma = classeDiploma;
    }

    //NB: ALL the following is added by Fanfa
    public String getClasseDiploma() {
        return this.classeDiploma;
    }

}

package it.uniroma2.sc.demospringhibernate.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TIPO")
@Data
public abstract class TitoloDiStudio {
    @Id
    @GeneratedValue
    private Long id;

    private String nomeTitolo;
    private int annoConseguimento;


    protected TitoloDiStudio() {

    }

    public TitoloDiStudio(String nomeTitolo, int annoConseguimento) {
        this.nomeTitolo = nomeTitolo;
        this.annoConseguimento = annoConseguimento;
    }

    //NB: ALL the following is added by Fanfa
    public Long getId() {
        return this.id;
    }

    public String getNomeTitolo() {
        return this.nomeTitolo;
    }

    public int getAnnoConseguimento() {
        return this.annoConseguimento;
    }

}

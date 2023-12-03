package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

@Data
public abstract class TitoloDiStudioDTO {
    private long id;
    private String nomeTitolo;
    private int annoConseguimento;

    public TitoloDiStudioDTO() {

    }

    public TitoloDiStudioDTO(long id, String nomeTitolo, int annoConseguimento) {
        this.id = id;
        this.nomeTitolo = nomeTitolo;
        this.annoConseguimento = annoConseguimento;

    }

    public long getId() {
        return this.id;
    }

    public String getNomeTitolo() {
        return this.nomeTitolo;
    }

    public int getAnnoConseguimento() {
        return this.annoConseguimento;
    }

}

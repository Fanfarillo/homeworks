package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

@Data
public class LaureaDTO extends TitoloDiStudioDTO {
    private boolean cicloUnico;

    public LaureaDTO() { super(); }

    public LaureaDTO(long id, String nomeTitolo, int annoConseguimento, boolean cicloUnico) {
        super(id, nomeTitolo, annoConseguimento);
        this.cicloUnico = cicloUnico;

    }

    public boolean isCicloUnico() {
        return this.cicloUnico;
    }

}

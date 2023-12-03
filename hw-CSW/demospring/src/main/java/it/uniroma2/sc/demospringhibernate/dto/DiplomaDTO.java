package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

@Data
public class DiplomaDTO extends TitoloDiStudioDTO {
    private String classeDiploma;

    public DiplomaDTO() { super(); }

    public DiplomaDTO(long id, String nomeTitolo, int annoConseguimento, String classeDiploma) {
        super(id, nomeTitolo, annoConseguimento);
        this.classeDiploma = classeDiploma;

    }

    public String getClasseDiploma() {
        return this.classeDiploma;
    }

}

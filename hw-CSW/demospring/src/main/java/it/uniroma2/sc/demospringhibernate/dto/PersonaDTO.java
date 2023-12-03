package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class PersonaDTO {
    private long id;
    private String nome;
    private String cognome;
    private IndirizzoDTO indirizzo;
    private List<TitoloDiStudioDTO> titoliDiStudio;

    public PersonaDTO() {

    }

    public PersonaDTO(long id, String nome, String cognome, IndirizzoDTO indirizzo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.titoliDiStudio = new LinkedList<>();

    }

    public void aggiungiTitolo(TitoloDiStudioDTO titoloDiStudio) {
        titoliDiStudio.add(titoloDiStudio);
    }


    public long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCognome() {
        return this.cognome;
    }

    public IndirizzoDTO getIndirizzo() {
        return this.indirizzo;
    }

    public List<TitoloDiStudioDTO> getTitoliDiStudio() {
        return this.titoliDiStudio;
    }

}

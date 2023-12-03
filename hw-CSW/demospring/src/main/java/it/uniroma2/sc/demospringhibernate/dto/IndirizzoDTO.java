package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

@Data
public class IndirizzoDTO {
    private long id;
    private String viaENumero;
    private String cap;
    private CittaDTO citta;

    public IndirizzoDTO() {

    }

    public IndirizzoDTO(long id, String viaENumero, String cap, CittaDTO citta) {
        this.id = id;
        this.viaENumero = viaENumero;
        this.cap = cap;
        this.citta = citta;

    }

    public long getId() {
        return this.id;
    }

    public String getViaENumero() {
        return this.viaENumero;
    }

    public String getCap() {
        return this.cap;
    }

    public CittaDTO getCitta() {
        return this.citta;
    }

}

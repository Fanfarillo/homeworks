package it.uniroma2.sc.demospringhibernate.dto;

import lombok.Data;

@Data
public class CittaDTO {
    private String nome;
    private String codiceIstat;
    private String codiceCatastale;

    public CittaDTO() {

    }

    public CittaDTO(String nome, String codiceIstat, String codiceCatastale) {
        this.nome = nome;
        this.codiceIstat = codiceIstat;
        this.codiceCatastale = codiceCatastale;

    }

    public String getNome() {
        return this.nome;
    }

    public String getCodiceIstat() {
        return this.codiceIstat;
    }

    public String getCodiceCatastale() {
        return this.codiceCatastale;
    }

}

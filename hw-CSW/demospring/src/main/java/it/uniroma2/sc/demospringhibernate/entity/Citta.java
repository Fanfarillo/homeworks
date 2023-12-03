package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Citta {

    private String nome;
    private String codiceIstat;
    private String codiceCatastale;

    //NB: ALL the following is added by Fanfa
    protected Citta() {

    }

    public Citta(String nome, String codiceIstat, String codiceCatastale) {
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


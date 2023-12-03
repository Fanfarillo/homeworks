package logic.entity;

public class Indirizzo {
    //attributes
    private final String nome;
    private String citta;

    //constructor
    public Indirizzo(String nome) {
        //se nome==null allora eventuali insert nel db non andranno a buon fine
        this.nome = nome;
    }

    //getters & setters
    public String getNome() {
        return this.nome;
    }

    public String getCitta() {
        return this.citta;
    }

    public void setCitta(String newCitta) {
        this.citta = newCitta;
    }

}

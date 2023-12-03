package logic.entity;

public class Cane {
    //attributes
    private final String matricola;
    private String nome;

    //constructor
    public Cane(String matricola) {
        //se matricola==null allora eventuali insert nel db non andranno a buon fine
        this.matricola = matricola;
    }

    //getters & setters
    public String getMatricola() {
        return this.matricola;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String newNome) {
        this.nome = newNome;
    }

}

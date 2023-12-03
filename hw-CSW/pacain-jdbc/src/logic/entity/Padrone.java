package logic.entity;

import java.util.ArrayList;
import java.util.List;

public class Padrone {
    //attributes
    private final String cf;
    private Indirizzo addr;
    private List<Cane> cani;

    //constructor
    public Padrone(String cf) {
        //se cf==null allora eventuali insert nel db non andranno a buon fine
        this.cf = cf;
        //definizione dell'ArrayList per il campo cani
        this.cani = new ArrayList<>();

    }

    //getters & setters
    public String getCf() {
        return this.cf;
    }

    public Indirizzo getAddr() {
        return this.addr;
    }

    public List<Cane> getCani() {
        return this.cani;
    }

    public void setAddr(Indirizzo newAddr) {
        this.addr = newAddr;
    }

    public void setCani(List<Cane> newCani) { this.cani = newCani; }

    //aggiunta di cani
    public void addCane(Cane newCane) { this.cani.add(newCane); }

}

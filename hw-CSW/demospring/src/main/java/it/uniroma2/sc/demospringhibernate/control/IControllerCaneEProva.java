package it.uniroma2.sc.demospringhibernate.control;

import it.uniroma2.sc.demospringhibernate.dto.CaneDTO;

import java.util.List;

public interface IControllerCaneEProva {
    CaneDTO creaCane(CaneDTO c);

    CaneDTO leggiCanePerId(Long idCane);

    List<CaneDTO> leggiCani();

    List<CaneDTO> cercaCaniPerNome(String nome);

    List<CaneDTO> cercaCaniPerPadrone(Long idPadrone) throws Exception;

    void creazioniDiProva();

    //NB: ALL the following is added by Fanfa.
    boolean rimuoviCane(Long idCane);

}

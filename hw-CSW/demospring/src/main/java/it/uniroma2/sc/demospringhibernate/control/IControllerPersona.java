package it.uniroma2.sc.demospringhibernate.control;

import it.uniroma2.sc.demospringhibernate.dto.PersonaDTO;

import java.util.List;

public interface IControllerPersona {

    PersonaDTO creaPersona(PersonaDTO p);

    PersonaDTO leggiPersonaPerId(Long idPersona);

    List<PersonaDTO> leggiPersone();

    boolean rimuoviPersona(Long idPersona);

}

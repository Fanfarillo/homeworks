package it.uniroma2.sc.demospringhibernate.mapper;

import it.uniroma2.sc.demospringhibernate.dto.*;
import it.uniroma2.sc.demospringhibernate.entity.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MappaPersona {

    public static Persona personaDTOToEntity(PersonaDTO dto) {
        //variabili d'appoggio
        Iterator<TitoloDiStudioDTO> iterTitolo = dto.getTitoliDiStudio().iterator();
        TitoloDiStudioDTO titoloDTO;
        LaureaDTO laureaDTO;
        DiplomaDTO diplomaDTO;

        Persona persona = new Persona(dto.getNome(), dto.getCognome(), MappaIndirizzo.indirizzoDTOToEntity(dto.getIndirizzo()));

        while(iterTitolo.hasNext()) {
            titoloDTO = iterTitolo.next();

            if(titoloDTO instanceof LaureaDTO) {
                laureaDTO = (LaureaDTO) titoloDTO;
                persona.aggiungiTitolo(MappaLaurea.laureaDTOToEntity(laureaDTO));

            } else {
                diplomaDTO = (DiplomaDTO) titoloDTO;
                persona.aggiungiTitolo(MappaDiploma.diplomaDTOToEntity(diplomaDTO));

            }

        }

        return persona;

    }



    public static PersonaDTO personaEntityToDTO(Persona entity) {
        //variabili d'appoggio
        Iterator<TitoloDiStudio> iterTitolo = entity.getTitoliDiStudio().iterator();
        TitoloDiStudio titolo;
        Laurea laurea;
        Diploma diploma;

        PersonaDTO dto = new PersonaDTO(entity.getId(), entity.getNome(), entity.getCognome(), MappaIndirizzo.indirizzoEntityToDTO(entity.getIndirizzo()));

        while(iterTitolo.hasNext()) {
            titolo = iterTitolo.next();

            if(titolo instanceof Laurea) {
                laurea = (Laurea) titolo;
                dto.aggiungiTitolo(MappaLaurea.laureaEntityToDTO(laurea));

            } else {
                diploma = (Diploma) titolo;
                dto.aggiungiTitolo(MappaDiploma.diplomaEntityToDTO(diploma));

            }

        }

        return dto;

    }



    public static List<PersonaDTO> personaEntityToDTO(List<Persona> persone) {
        //variabili d'appoggio
        Iterator<Persona> iter = persone.iterator();
        Persona p;

        List<PersonaDTO> personeDTO = new LinkedList<>();
        while(iter.hasNext()) {
            p = iter.next();
            personeDTO.add(personaEntityToDTO(p));
        }
        return personeDTO;

    }

}

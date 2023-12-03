package it.uniroma2.sc.demospringhibernate.mapper;

import it.uniroma2.sc.demospringhibernate.dto.CaneDTO;
import it.uniroma2.sc.demospringhibernate.entity.Cane;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MappaCane {

    public static Cane caneDTOToEntity(CaneDTO dto) {
        return new Cane(dto.getId(), dto.getNome(), MappaPersona.personaDTOToEntity(dto.getPadrone()));
    }

    public static CaneDTO caneEntityToDTO(Cane entity) {
        return new CaneDTO(entity.getId(), entity.getNome(), MappaPersona.personaEntityToDTO(entity.getPadrone()));
    }

    public static List<CaneDTO> caneEntityToDTO(List<Cane> cani) {
        //variabili d'appoggio
        Iterator<Cane> iter = cani.iterator();
        Cane c;

        List<CaneDTO> caniDTO = new LinkedList<>();
        while(iter.hasNext()) {
            c = iter.next();
            caniDTO.add(caneEntityToDTO(c));
        }
        return caniDTO;

    }

}

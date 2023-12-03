package it.uniroma2.sc.demospringhibernate.mapper;

import it.uniroma2.sc.demospringhibernate.dto.LaureaDTO;
import it.uniroma2.sc.demospringhibernate.entity.Laurea;

public class MappaLaurea {
    public static Laurea laureaDTOToEntity(LaureaDTO dto) {
        return new Laurea(dto.getNomeTitolo(), dto.getAnnoConseguimento(), dto.isCicloUnico());
    }

    public static LaureaDTO laureaEntityToDTO(Laurea entity) {
        return new LaureaDTO(entity.getId(), entity.getNomeTitolo(), entity.getAnnoConseguimento(), entity.isCicloUnico());
    }

}

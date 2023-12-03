package it.uniroma2.sc.demospringhibernate.mapper;

import it.uniroma2.sc.demospringhibernate.dto.DiplomaDTO;
import it.uniroma2.sc.demospringhibernate.entity.Diploma;

public class MappaDiploma {
    public static Diploma diplomaDTOToEntity(DiplomaDTO dto) {
        return new Diploma(dto.getNomeTitolo(), dto.getAnnoConseguimento(), dto.getClasseDiploma());
    }

    public static DiplomaDTO diplomaEntityToDTO(Diploma entity) {
        return new DiplomaDTO(entity.getId(), entity.getNomeTitolo(), entity.getAnnoConseguimento(), entity.getClasseDiploma());
    }

}

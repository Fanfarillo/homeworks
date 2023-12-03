package it.uniroma2.sc.demospringhibernate.mapper;

import it.uniroma2.sc.demospringhibernate.dto.CittaDTO;
import it.uniroma2.sc.demospringhibernate.entity.Citta;

public class MappaCitta {
    public static Citta cittaDTOToEntity(CittaDTO dto) {
        return new Citta(dto.getNome(), dto.getCodiceIstat(), dto.getCodiceCatastale());
    }

    public static CittaDTO cittaEntityToDTO(Citta entity) {
        return new CittaDTO(entity.getNome(), entity.getCodiceIstat(), entity.getCodiceCatastale());
    }

}

package it.uniroma2.sc.demospringhibernate.mapper;

import it.uniroma2.sc.demospringhibernate.dto.IndirizzoDTO;
import it.uniroma2.sc.demospringhibernate.entity.Indirizzo;

public class MappaIndirizzo {

    public static Indirizzo indirizzoDTOToEntity(IndirizzoDTO dto) {
        return new Indirizzo(dto.getViaENumero(), dto.getCap(), MappaCitta.cittaDTOToEntity(dto.getCitta()));
    }

    public static IndirizzoDTO indirizzoEntityToDTO(Indirizzo entity) {
        return new IndirizzoDTO(entity.getId(), entity.getViaENumero(), entity.getCap(), MappaCitta.cittaEntityToDTO(entity.getCitta()));
    }

}

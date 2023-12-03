package it.uniroma2.sc.demospringhibernate.control;

import it.uniroma2.sc.demospringhibernate.dao.DiplomaDao;
import it.uniroma2.sc.demospringhibernate.dao.LaureaDao;
import it.uniroma2.sc.demospringhibernate.dao.PersonaDao;

import it.uniroma2.sc.demospringhibernate.dto.PersonaDTO;
import it.uniroma2.sc.demospringhibernate.entity.Persona;
import it.uniroma2.sc.demospringhibernate.mapper.MappaPersona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class ControllerPersona implements IControllerPersona {

    @Autowired
    private DiplomaDao diplomaDao;

    @Autowired
    private LaureaDao laureaDao;

    @Autowired
    private PersonaDao personaDao;

    public PersonaDTO creaPersona(@NotNull PersonaDTO p) {
        Persona entity = MappaPersona.personaDTOToEntity(p);
        return MappaPersona.personaEntityToDTO(personaDao.save(entity));

    }

    public PersonaDTO leggiPersonaPerId(@NotNull Long idPersona) {
        return MappaPersona.personaEntityToDTO(personaDao.getOne(idPersona));
    }

    public List<PersonaDTO> leggiPersone() {
        return MappaPersona.personaEntityToDTO(personaDao.findAll());
    }

    public boolean rimuoviPersona(Long idPersona) {
        Optional<Persona> personaOpt = personaDao.findById(idPersona);
        if(personaOpt.isEmpty())
            return false;

        personaDao.deleteById(idPersona);
        return true;
    }

}

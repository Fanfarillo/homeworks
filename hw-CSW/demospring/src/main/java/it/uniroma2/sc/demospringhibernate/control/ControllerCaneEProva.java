package it.uniroma2.sc.demospringhibernate.control;

import it.uniroma2.sc.demospringhibernate.dao.CaneDao;
import it.uniroma2.sc.demospringhibernate.dao.DiplomaDao;
import it.uniroma2.sc.demospringhibernate.dao.LaureaDao;
import it.uniroma2.sc.demospringhibernate.dao.PersonaDao;
import it.uniroma2.sc.demospringhibernate.dto.CaneDTO;
import it.uniroma2.sc.demospringhibernate.entity.*;
import it.uniroma2.sc.demospringhibernate.mapper.MappaCane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class ControllerCaneEProva implements IControllerCaneEProva {

    @Autowired
    private CaneDao caneDao;

    @Autowired
    private DiplomaDao diplomaDao;

    @Autowired
    private LaureaDao laureaDao;

    @Autowired
    private PersonaDao personaDao;

    @Transactional
    public void creazioniDiProva() {    //NB: lascio questo metodo invariato perché qui le entity non viaggiano verso alcun endpoint.
        //NB: l'unica cosa che aggiungo è la definizione di una città
        Citta citta = new Citta("roma", "codiceIstat", "codiceCatastale");
        Indirizzo indirizzo = new Indirizzo("via e numero", "00100", citta);
        Persona p = new Persona("nome", "cognome", indirizzo);

        Laurea l = new Laurea("nome titolo", 2010, false);
        Diploma d = new Diploma("nome titolo", 2010, "classe titolo");

        p.aggiungiTitolo(l);
        p.aggiungiTitolo(d);

        p = personaDao.save(p);

        Cane c0 = new Cane("Bobby2", p);
        caneDao.save(c0);

        Cane c1 = new Cane("Bobby3", p);
        caneDao.save(c1);

        List<Cane> caniSalvati = caneDao.findAll();
        for (Cane c : caniSalvati) {
            System.out.println(c);
        }

        List<Cane> caniBobby = caneDao.findByNome("Bobby2");

        List<Cane> caniPerPadrone = caneDao.findByPadrone(p);

        List<Cane> caniPerCognomePadroneEsistente = caneDao.findByPadrone_CognomeLike("cogn%");
        List<Cane> caniPerCognomePadroneNonEsistente = caneDao.findByPadrone_CognomeLike("Bobby%");

    }

    //NB: modified by Fanfa for DTO utilization
    public CaneDTO creaCane(@NotNull CaneDTO c) {
        Cane entity = MappaCane.caneDTOToEntity(c);
        personaDao.save(entity.getPadrone());
        return MappaCane.caneEntityToDTO(caneDao.save(entity));

    }

    //NB: modified by Fanfa for DTO utilization
    public CaneDTO leggiCanePerId(@NotNull Long idCane) {
        return MappaCane.caneEntityToDTO(caneDao.getOne(idCane));
    }

    //NB: modified by Fanfa for DTO utilization
    public List<CaneDTO> leggiCani() {
        return MappaCane.caneEntityToDTO(caneDao.findAll());
    }

    //NB: modified by Fanfa for DTO utilizatio
    /**
     * @param nome non deve essere null
     * @return
     */
    public List<CaneDTO> cercaCaniPerNome(@NotNull String nome) {
        return MappaCane.caneEntityToDTO(caneDao.findByNome(nome));
    }

    //NB: modified by Fanfa for DTO utilizatio
    public List<CaneDTO> cercaCaniPerPadrone(@NotNull Long idPadrone) throws Exception {
        Persona padrone = personaDao.getOne(idPadrone);
        if(padrone==null) {
            throw new Exception("Nessun padrone presente con id " + idPadrone);
        }
        return MappaCane.caneEntityToDTO(caneDao.findByPadrone(padrone));
    }

    //NB: ALL the following is added by Fanfa.
    public boolean rimuoviCane(Long idCane) {
        Optional<Cane> caneOpt = caneDao.findById(idCane);
        if(caneOpt.isEmpty())
            return false;

        caneDao.deleteById(idCane);
        return true;
    }

}

package it.uniroma2.sc.demospringhibernate.rest;

import it.uniroma2.sc.demospringhibernate.control.IControllerCaneEProva;
import it.uniroma2.sc.demospringhibernate.dto.CaneDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cane/")
public class CaneRESTEndpoint {

    @Autowired
    private IControllerCaneEProva controllerCaneEProva;

    //NB: modified by Fanfa for DTO utilization
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaCane(@RequestBody(required = true) CaneDTO c) {
        if (c != null) {
            CaneDTO newCane = controllerCaneEProva.creaCane(c);
            return new ResponseEntity<>(newCane, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //NB: modified by Fanfa for DTO utilization
    @RequestMapping(method = RequestMethod.GET, path = "{idCane}")
    public ResponseEntity<?> leggiCane(@PathVariable Long idCane) {
        if (idCane != null) {
            CaneDTO c = controllerCaneEProva.leggiCanePerId(idCane);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //NB: modified by Fanfa for DTO utilization
    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<?> leggiCani() {
        List<CaneDTO> tuttiICani = controllerCaneEProva.leggiCani();
        return new ResponseEntity<>(tuttiICani, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "search") // /api/cane/search?nomeCane=Bobby
    public ResponseEntity<?> cercaCaniPerNome(@RequestParam(name = "nomeCane", required = false) String nome) {
        if (nome != null) {
            return new ResponseEntity<>(controllerCaneEProva.cercaCaniPerNome(nome), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "padrone/{id}")
    public ResponseEntity<?> cercaCaniPerPadrone(@PathVariable(name = "id") Long idPadrone) {
        try {
            return new ResponseEntity<>(controllerCaneEProva.cercaCaniPerPadrone(idPadrone), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "generate")
    public ResponseEntity<Void> generateSampleData() {
        controllerCaneEProva.creazioniDiProva();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //NB: ALL the following is added by Fanfa.
    @RequestMapping(method = RequestMethod.DELETE, path = "{idCane}")
    public ResponseEntity<?> rimuoviCane(@PathVariable Long idCane) {
        if(idCane != null) {
            if(controllerCaneEProva.rimuoviCane(idCane)) {
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}

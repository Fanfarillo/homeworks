package it.uniroma2.sc.demospringhibernate.rest;

import it.uniroma2.sc.demospringhibernate.control.IControllerPersona;
import it.uniroma2.sc.demospringhibernate.dto.PersonaDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persona/")
public class PersonaRESTEndpoint {

    @Autowired
    private IControllerPersona controllerPersona;

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> creaPersona(@RequestBody(required = true) PersonaDTO p) {
        if (p != null) {
            PersonaDTO newPersona = controllerPersona.creaPersona(p);
            return new ResponseEntity<>(newPersona, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "{idPersona}")
    public ResponseEntity<?> leggiPersona(@PathVariable Long idPersona) {
        if (idPersona != null) {
            PersonaDTO p = controllerPersona.leggiPersonaPerId(idPersona);
            if (p == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(p, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<?> leggiPersone() {
        List<PersonaDTO> tutteLePersone = controllerPersona.leggiPersone();
        return new ResponseEntity<>(tutteLePersone, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "{idPersona}")
    public ResponseEntity<?> rimuoviPersona(@PathVariable Long idPersona) {
        if(idPersona != null) {
            if(controllerPersona.rimuoviPersona(idPersona)) {
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}

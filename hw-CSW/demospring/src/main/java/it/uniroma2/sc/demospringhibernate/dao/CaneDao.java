package it.uniroma2.sc.demospringhibernate.dao;

import it.uniroma2.sc.demospringhibernate.entity.Cane;
import it.uniroma2.sc.demospringhibernate.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CaneDao  extends JpaRepository<Cane,Long> {
    List<Cane> findByNome(String nome);
    List<Cane> findByPadrone(Persona padrone);
    List<Cane> findByPadrone_CognomeLike(String cognome);
}

package it.uniroma2.sc.demospringhibernate.dao;

import it.uniroma2.sc.demospringhibernate.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonaDao extends JpaRepository<Persona,Long> {

    List<Persona> findByNomeAndCognome(String n, String c);
    List<Persona> findByNomeIn(String[] nomi);


    @Query(nativeQuery = true, value = "select * from Persona where nome is null or nome=:mioParam")
    List<Persona> findConCondizioniStrane(@Param("mioParam") Long p);

}

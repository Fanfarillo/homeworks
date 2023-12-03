package it.uniroma2.sc.demospringhibernate.dao;

import it.uniroma2.sc.demospringhibernate.entity.Diploma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiplomaDao extends JpaRepository<Diploma,Long> {
}

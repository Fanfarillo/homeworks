package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Entity
public class FigliaMappedSuperclass extends MadreMappedSuperclass {

    private int numero;
}

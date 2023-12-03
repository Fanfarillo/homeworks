package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
public class FigliaConcretaTablePerClass extends FigliaAstrattaTablePerClass{

    private int altroNumeroAncora;
}

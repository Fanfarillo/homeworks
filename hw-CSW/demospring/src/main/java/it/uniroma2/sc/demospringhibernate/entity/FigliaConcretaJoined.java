package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.Entity;

@Entity
public abstract class FigliaConcretaJoined extends FigliaAstrattaJoined {

    private int altroNumeroAncora;
}

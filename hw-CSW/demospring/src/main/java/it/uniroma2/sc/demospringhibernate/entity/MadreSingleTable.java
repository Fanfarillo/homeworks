package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class MadreSingleTable {

    @Id
    @GeneratedValue
    private Long id;

    private int numero;
}

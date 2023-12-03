package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.*;

@MappedSuperclass
public class MadreMappedSuperclass {

    @Id
    @GeneratedValue
    private Long id;

    private int numero;
}

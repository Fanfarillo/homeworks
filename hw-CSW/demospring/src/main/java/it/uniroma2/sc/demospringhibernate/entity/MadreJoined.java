package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class MadreJoined {

    @Id
    @GeneratedValue
    private Long id;

    private int numero;
}

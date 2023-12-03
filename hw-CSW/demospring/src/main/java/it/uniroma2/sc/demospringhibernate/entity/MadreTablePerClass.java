package it.uniroma2.sc.demospringhibernate.entity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MadreTablePerClass {

    @Id
    @GeneratedValue
    private Long id;

    private int numero;
}

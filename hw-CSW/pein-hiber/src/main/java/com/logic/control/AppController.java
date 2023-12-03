package com.logic.control;

import com.logic.entity.Person;
import com.logic.entity.Address;
import com.logic.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class AppController {
    //attributes
    Scanner scanner;

    //constructor
    public AppController() {
        this.scanner = new Scanner(System.in);
    }

    public void addAddress() {
        //name and city acquisition
        System.out.println("What is the name of the address? [No apostrophe allowed]");
        String name = this.scanner.nextLine();
        System.out.println("What is the city? [No apostrophe allowed]");
        String city = this.scanner.nextLine();

        //retrieve della sessione Hibernate
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        //istanziazione e inserimento nel DB del nuovo indirizzo
        Address newAddress = new Address();
        newAddress.setName(name);
        newAddress.setCity(city);

        Transaction transaction = session.beginTransaction();
        session.save(newAddress);
        transaction.commit();

        System.out.println("Operation completed.");

    }

    public void viewAddresses() {
        //definizione degli oggetti necessari
        Address addr;

        //retrieve della sessione Hibernate
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        //retrieve degli indirizzi dal DB
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("from Address");
        List<Address> readAddresses = query.list();
        transaction.commit();

        //print read data
        Iterator<Address> iter = readAddresses.iterator();
        System.out.println("ADDRESS NAME - CITY");
        while(iter.hasNext()) {
            addr = iter.next();
            System.out.println(addr.getName() + " - " + addr.getCity());

        }

    }

    public void addPerson() {
        //definizione degli oggetti necessari
        Address residence;
        Address selectedResidence = null;
        Address selectedDomicile;
        List<Address> allSelectedDomiciles;

        Transaction transaction;
        Query query;

        //retrieve della sessione Hibernate
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        //cf acquisition
        System.out.println("What is the cf of the person? [No apostrophe allowed]");
        String cf = this.scanner.nextLine();

        //residence acquisition
        System.out.println("What is the residence of the person? [Select ONE option]");

        transaction = session.beginTransaction();
        query = session.createQuery("from Address");
        List<Address> allAddresses = query.list();
        transaction.commit();

        Iterator<Address> iter = allAddresses.iterator();
        int i = 1;  //indice opzioni
        while(iter.hasNext()) {
            residence = iter.next();
            System.out.println(i + ") " + residence.getName() + " - " + residence.getCity());
            i++;

        }
        System.out.println("Other number) NONE");   //è possibile non assegnare alcuna residenza alla nuova persona.

        int selectedOpt = this.scanner.nextInt();

        if(selectedOpt > 0 && selectedOpt < i) {  //caso in cui è stata effettivamente selezionata una residenza
            int j = 1;
            iter = allAddresses.iterator();
            while(iter.hasNext()) {
                selectedResidence = iter.next();
                if(j == selectedOpt) {
                    break;
                }
                j++;

            }

        }
        //nel caso in cui è stato selezionato NONE, selectedResidence rimane null.

        //domiciles
        System.out.println("How many domiciles does the person have?");
        int numDomiciles = this.scanner.nextInt();

        //caso in cui la persona non ha domicili
        if(numDomiciles <= 0) {
            allSelectedDomiciles = null;
        }

        //caso in cui la persona ha dei domicili (ma non sono tutti quelli registrati nel DB)
        else if(numDomiciles < i-1) {
            allSelectedDomiciles = new ArrayList<>();

            for(int k=0; k<numDomiciles; k++) {
                System.out.println("Select domicile " + (k+1) + ":");
                selectedOpt = this.scanner.nextInt();

                if(selectedOpt > 0 && selectedOpt < i) {  //caso in cui è stato effettivamente selezionato un domicilio
                    int h = 1;
                    iter = allAddresses.iterator();
                    while(iter.hasNext()) {
                        selectedDomicile = iter.next();
                        if(h == selectedOpt) {
                            allSelectedDomiciles.add(selectedDomicile);
                            break;
                        }
                        h++;

                    }

                }
                else {
                    System.out.println("Sorry, the provided input is incorrect. Please try again.");
                    k--;    //roll-back dell'iterazione appena effettuata
                }

            }

        }

        //caso in cui la persona ha tutti i domicili registrati nel DB
        else {
            allSelectedDomiciles = allAddresses;
        }

        //è solo a questo punto che abbiamo i valori di tutti gli attributi della persona da istanziare.
        //istanziazione e inserimento nel DB della nuova persona
        Person newPerson = new Person();
        newPerson.setCf(cf);
        newPerson.setResidence(selectedResidence);
        newPerson.setDomiciles(allSelectedDomiciles);

        //retrieve della sessione Hibernate
        session = HibernateUtil.getSessionFactory().getCurrentSession();

        //insertion into DB
        transaction = session.beginTransaction();
        session.save(newPerson);
        transaction.commit();

        System.out.println("Operation completed.");

    }

    public void viewPeople() {
        //definizione degli oggetti necessari
        Person person;
        Address domicile;

        //retrieve della sessione Hibernate
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        //read from DB
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("from Person P");
        List<Person> readPeople = query.list();
        transaction.commit();

        //print read data
        Iterator<Person> iter = readPeople.iterator();
        Iterator<Address> iter2;
        System.out.println("CF - RESIDENCE - CITY");

        while(iter.hasNext()) {
            person = iter.next();
            if(person.getResidence() != null) {
                System.out.println("\n" + person.getCf() + " - " + person.getResidence().getName() + " - " + person.getResidence().getCity());
            }
            else {
                System.out.println("\n" + person.getCf() + " - NONE - NONE");
            }
            System.out.println("LIST OF DOMICILES: NAME - CITY");

            if(person.getDomiciles() != null) {
                iter2 = person.getDomiciles().iterator();
                while(iter2.hasNext()) {
                    domicile = iter2.next();
                    System.out.println(domicile.getName() + " - " + domicile.getCity());

                }

            }

        }

    }

}

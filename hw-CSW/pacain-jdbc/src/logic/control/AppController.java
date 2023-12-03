package logic.control;

import logic.dao.CaneDAO;
import logic.dao.IndirizzoDAO;
import logic.dao.PadroneDAO;
import logic.entity.Cane;
import logic.entity.Indirizzo;
import logic.entity.Padrone;

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

        //istanziazione degli oggetti necessari
        IndirizzoDAO daoAddress = new IndirizzoDAO();
        Indirizzo newAddress = new Indirizzo(name);
        newAddress.setCitta(city);

        //insertion into DB
        daoAddress.insertAddressDB(newAddress);
        System.out.println("Operation completed.");

    }

    public void viewAddresses() {
        //definizione degli oggetti necessari
        IndirizzoDAO daoAddress = new IndirizzoDAO();
        Indirizzo addr;

        //read from DB
        List<Indirizzo> readAddresses = daoAddress.selectAddressDB();
        Iterator<Indirizzo> iter = readAddresses.iterator();

        //print read data
        System.out.println("ADDRESS NAME - CITY");
        while(iter.hasNext()) {
            addr = iter.next();
            System.out.println(addr.getNome() + " - " + addr.getCitta());

        }

    }

    public void addDog() {
        //matriculation number and name acquisition
        System.out.println("What is the matriculation number of the dog? [No apostrophe allowed]");
        String matr = this.scanner.nextLine();
        System.out.println("What is the name of the dog? [No apostrophe allowed]");
        String name = this.scanner.nextLine();

        //istanziazione degli oggetti necessari
        CaneDAO daoDog = new CaneDAO();
        Cane newDog = new Cane(matr);
        newDog.setNome(name);

        //insertion into DB
        daoDog.insertDogDB(newDog);
        System.out.println("Operation completed.");

    }

    public void viewDogs() {
        //definizione degli oggetti necessari
        CaneDAO daoDog = new CaneDAO();
        Cane dog;

        //read from DB
        List<Cane> readDogs = daoDog.selectDogDB();
        Iterator<Cane> iter = readDogs.iterator();

        //print read data
        System.out.println("MATRICULATION NUMBER - NAME");
        while(iter.hasNext()) {
            dog = iter.next();
            System.out.println(dog.getMatricola() + " - " + dog.getNome());

        }

    }

    public void addPerson() {
        //definizione degli oggetti necessari
        IndirizzoDAO daoAddress = new IndirizzoDAO();
        Indirizzo addr;
        Indirizzo selectedAddr = null;

        CaneDAO daoDog = new CaneDAO();
        Cane dog;
        Cane selectedDog;
        List<Cane> allSelectedDogs;

        //cf acquisition
        System.out.println("What is the cf of the person? [No apostrophe allowed]");
        String cf = this.scanner.nextLine();

        //address acquisition
        System.out.println("What is the address of the person? [Select ONE option]");
        List<Indirizzo> allAddresses = daoAddress.selectAddressDB();
        Iterator<Indirizzo> iter = allAddresses.iterator();

        int i = 1;  //indice opzioni
        while(iter.hasNext()) {
            addr = iter.next();
            System.out.println(i + ") " + addr.getNome() + " - " + addr.getCitta());
            i++;

        }
        System.out.println("Other number) NONE");   //è possibile non assegnare alcun inidirizzo al nuovo padrone.

        int selectedOpt = this.scanner.nextInt();

        if(selectedOpt > 0 && selectedOpt < i) {  //caso in cui è stato effettivamente selezionato un indirizzo
            int j = 1;
            iter = allAddresses.iterator();
            while(iter.hasNext()) {
                selectedAddr = iter.next();
                if(j == selectedOpt) {
                    break;
                }
                j++;

            }

        }
        //nel caso in cui è stato selezionato NONE, selectedAddr rimane null.

        //dogs acquisition
        List<Cane> allDogs = daoDog.selectDogDB();
        Iterator<Cane> iter2 = allDogs.iterator();

        System.out.println("LIST OF DOGS: MATRICULATION NUMBER - NAME");
        i = 1;  //indice opzioni
        while(iter2.hasNext()) {
            dog = iter2.next();
            System.out.println(i + ") " + dog.getMatricola() + " - " + dog.getNome());
            i++;

        }   //a questo punto i-1 è il numero totale dei cani.

        System.out.println("How many dogs does the person have?");
        int numDogs = this.scanner.nextInt();

        //caso in cui il padrone non ha cani
        if(numDogs <= 0) {
            allSelectedDogs = null;
        }

        //caso in cui il padrone ha dei cani (ma non sono tutti quelli registrati nel DB)
        else if(numDogs < i-1) {
            allSelectedDogs = new ArrayList<>();

            for(int k=0; k<numDogs; k++) {
                System.out.println("Select dog " + (k+1) + ":");
                selectedOpt = this.scanner.nextInt();

                if(selectedOpt > 0 && selectedOpt < i) {  //caso in cui è stato effettivamente selezionato un cane
                    int h = 1;
                    iter2 = allDogs.iterator();
                    while(iter2.hasNext()) {
                        selectedDog = iter2.next();
                        if(h == selectedOpt) {
                            allSelectedDogs.add(selectedDog);
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

        //caso in cui il padrone ha tutti i cani registrati nel DB
        else {
            allSelectedDogs = allDogs;
        }

        //è solo a questo punto che abbiamo i valori di tutti gli attributi del padrone da istanziare.
        //istanziazione degli oggetti necessari
        PadroneDAO daoPerson = new PadroneDAO();
        Padrone newPerson = new Padrone(cf);
        newPerson.setAddr(selectedAddr);
        newPerson.setCani(allSelectedDogs);

        //insertion into DB
        daoPerson.insertPersonDB(newPerson);
        System.out.println("Operation completed.");

    }

    public void viewPeople() {
        //definizione degli oggetti necessari
        PadroneDAO daoPerson = new PadroneDAO();
        Padrone person;
        Cane dog;

        //read from DB
        List<Padrone> readPeople = daoPerson.selectPersonDB();
        Iterator<Padrone> iter = readPeople.iterator();
        Iterator<Cane> iter2;

        //print read data
        System.out.println("CF - ADDRESS - CITY");

        while(iter.hasNext()) {
            person = iter.next();
            if(person.getAddr() != null) {
                System.out.println("\n" + person.getCf() + " - " + person.getAddr().getNome() + " - " + person.getAddr().getCitta());
            }
            else {
                System.out.println("\n" + person.getCf() + " - NONE - NONE");
            }
            System.out.println("LIST OF DOGS: MATRICULATION NUMBER - NAME");

            if(person.getCani() != null) {
                iter2 = person.getCani().iterator();
                while(iter2.hasNext()) {
                    dog = iter2.next();
                    System.out.println(dog.getMatricola() + " - " + dog.getNome());

                }

            }

        }

    }

}

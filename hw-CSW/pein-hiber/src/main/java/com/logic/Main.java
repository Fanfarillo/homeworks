package com.logic;

import com.logic.control.AppController;
import com.logic.utils.HibernateUtil;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("HI! THIS IS PEIN-HIBER");
        //istanziazione degli oggetti necessari
        Scanner scanner = new Scanner(System.in);
        AppController controller = new AppController();
        try {
            HibernateUtil.getSessionFactory().openSession();

        }
        catch (Throwable ex) {
            System.err.println("Initial objects instantiation failed: " + ex);
            return;

        }

        while(true) {
            System.out.println("\n\nWhat should I do for you?");
            System.out.println("1) Add a new address");
            System.out.println("2) Add a new person");
            System.out.println("3) View all the addresses");
            System.out.println("4) View all the people");
            System.out.println("5) Quit");

            char selectedCommand = scanner.next().charAt(0);

            switch(selectedCommand) {
                case '1':
                    controller.addAddress();
                    break;

                case '2':
                    controller.addPerson();
                    break;

                case '3':
                    controller.viewAddresses();
                    break;

                case '4':
                    controller.viewPeople();
                    break;

                case '5':
                    System.out.println("BYE!");
                    scanner.close();
                    HibernateUtil.getSessionFactory().close();
                    return;

                default:
                    System.out.println("Sorry, the provided input is incorrect. Please try again.");
                    break;

            }

        }

    }

}

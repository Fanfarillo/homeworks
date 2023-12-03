package logic;

import logic.control.AppController;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("HI! THIS IS PACAIN-JDBC");
        //istanziazione degli oggetti necessari
        Scanner scanner = new Scanner(System.in);
        AppController controller = new AppController();

        while(true) {
            System.out.println("\n\nWhat should I do for you?");
            System.out.println("1) Add a new address");
            System.out.println("2) Add a new dog");
            System.out.println("3) Add a new person");
            System.out.println("4) View all the addresses");
            System.out.println("5) View all the dogs");
            System.out.println("6) View all the people");
            System.out.println("7) Quit");

            char selectedCommand = scanner.next().charAt(0);

            switch(selectedCommand) {
                case '1':
                    controller.addAddress();
                    break;

                case '2':
                    controller.addDog();
                    break;

                case '3':
                    controller.addPerson();
                    break;

                case '4':
                    controller.viewAddresses();
                    break;

                case '5':
                    controller.viewDogs();
                    break;

                case '6':
                    controller.viewPeople();
                    break;

                case '7':
                    System.out.println("BYE!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Sorry, the provided input is incorrect. Please try again.");
                    break;

            }

        }

    }

}

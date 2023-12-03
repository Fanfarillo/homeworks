package logic;

import logic.entity.Birra;
import logic.entity.Bottiglia;
import logic.entity.BraccioAutomatico;
import logic.entity.Vino;

public class Main {

    public static void main(String[] args) {
        System.out.println("Salve!");

        Bottiglia<Birra> bottle1 = new Bottiglia<>(new Birra());
        Bottiglia<Vino> bottle2 = new Bottiglia<>(new Vino());
        BraccioAutomatico braccio = new BraccioAutomatico();

        System.out.println("\nOra il braccio automatico si comporterà bene.");
        braccio.prendiBottiglia(bottle1);
        braccio.posaBottiglia(bottle1);
        braccio.prendiBottiglia(bottle2);
        braccio.posaBottiglia(bottle2);

        System.out.println("\nOra il braccio automatico si comporterà meno bene.");
        braccio.posaBottiglia(bottle1);
        braccio.prendiBottiglia(bottle2);
        braccio.prendiBottiglia(bottle1);
        braccio.posaBottiglia(bottle1);
        braccio.posaBottiglia(bottle2);

    }

}

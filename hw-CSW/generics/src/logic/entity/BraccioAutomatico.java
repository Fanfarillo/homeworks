package logic.entity;

import logic.enums.Stato;

public class BraccioAutomatico {
    private Stato stato;   //attributo che indica lo stato del braccio (se ha una bottiglia ed eventualmente quale)

    public BraccioAutomatico() {
        this.stato = Stato.LIBERO;
    }

    public void prendiBottiglia(Bottiglia<?> bottle) {
        if(this.stato != Stato.LIBERO) {
            System.out.println("Impossibile prendere " + bottle.getContenuto() + ": il braccio è già occupato.");
        } else if(bottle.getContenuto() instanceof Birra) {
            this.stato = Stato.BIRRA;
            System.out.println("Ho preso " + bottle.getContenuto() + ".");
        } else {
            this.stato = Stato.VINO;
            System.out.println("Ho preso " + bottle.getContenuto() + ".");
        }

    }

    public void posaBottiglia(Bottiglia<?> bottle) {
        if((this.stato == Stato.BIRRA && bottle.getContenuto() instanceof Birra) || (this.stato == Stato.VINO && bottle.getContenuto() instanceof Vino)) {
            this.stato = Stato.LIBERO;
            System.out.println("Ho posato " + bottle.getContenuto() + ".");
        } else {
            System.out.println("Non ho " + bottle.getContenuto() + " da posare.");
        }

    }

}

package logic.utils;

import logic.annots.PizzaAnnotation;
import logic.enums.SizeEnum;

@PizzaAnnotation(flavour="Diavola", size=SizeEnum.LARGE)
public class AnnotUse {
    @PizzaAnnotation(flavour="Diavola", size=SizeEnum.LARGE)
    public String description;

    //constructor
    @PizzaAnnotation(flavour="Diavola", size=SizeEnum.LARGE)
    public AnnotUse() {
        this.description = "MY PIZZA:";
    }

    //getter method
    @PizzaAnnotation(flavour="Diavola", size=SizeEnum.LARGE)
    public String getDescription() {
        return  this.description;
    }

}

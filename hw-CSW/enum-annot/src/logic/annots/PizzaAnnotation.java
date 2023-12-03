package logic.annots;

import logic.enums.SizeEnum;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface PizzaAnnotation {
    String flavour();
    SizeEnum size();

}

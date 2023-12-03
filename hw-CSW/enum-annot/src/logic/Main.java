package logic;

import logic.annots.PizzaAnnotation;
import logic.utils.AnnotUse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException {
        //accesso alla classe
        Class<AnnotUse> c = AnnotUse.class;

        //mostra annotazioni
        System.out.println("Annotazione di classe: ");
        System.out.println(c.getAnnotation(PizzaAnnotation.class));

        //metodo costruttore
        Constructor<AnnotUse> constructor = c.getConstructor((Class[]) null);
        System.out.println("Annotazione costruttore: ");
        System.out.println(constructor.getAnnotation(PizzaAnnotation.class));

        //metodo getDescription
        Method method = c.getMethod( "getDescription");
        System.out.println("Annotazione metodo getSaluto(): ");
        System.out.println(method.getAnnotation(PizzaAnnotation.class));

        //campo description
        Field field = c.getField("description");
        System.out.println("Annotazione attributo saluto: ");
        System.out.println(field.getAnnotation(PizzaAnnotation.class));

        //stampa della descrizione della mia pizza
        AnnotUse annotUse = new AnnotUse();
        System.out.println("\n" + annotUse.getDescription());
        System.out.println("Flavour: " + c.getAnnotation(PizzaAnnotation.class).flavour());
        System.out.println("Size: " + c.getAnnotation(PizzaAnnotation.class).size());

    }

}

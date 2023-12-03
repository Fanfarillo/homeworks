package logic.entity;

public class Bottiglia<T> {
    private final T contenuto;

    public Bottiglia(T t) {
        this.contenuto = t;
    }

    public T getContenuto() {
        return this.contenuto;
    }

}

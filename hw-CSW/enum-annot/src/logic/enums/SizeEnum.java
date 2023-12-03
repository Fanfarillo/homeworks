package logic.enums;

public enum SizeEnum {
    //enum constants calling the enum constructors
    SMALL("The size is small."),
    MEDIUM("The size is medium."),
    LARGE("The size is large."),
    EXTRALARGE("The size is extra-large.");

    private final String size;

    //private enum constructor
    SizeEnum(String size) {
        this.size = size;
    }

    //simple getter method
    public String getSize() {
        return this.size;
    }

}

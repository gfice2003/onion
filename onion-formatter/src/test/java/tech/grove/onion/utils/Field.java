package tech.grove.onion.utils;

public record Field(String name, Object value) {

    public static Field of(String name, Object value) {
        return new Field(name, value);
    }
}

package tech.grove.onion.api.shell;

public interface Adder {

    default Report add() {
        return add(null);
    }

    Report add(String pattern, Object... parameters);
}

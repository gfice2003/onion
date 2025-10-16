package tech.grove.onion.api.layer;

public interface DefaultAdder extends Adder {

    default void add() {
        add(null);
    }
}
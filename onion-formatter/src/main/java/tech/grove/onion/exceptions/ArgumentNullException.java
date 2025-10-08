package tech.grove.onion.exceptions;

public class ArgumentNullException extends RuntimeException {

    public ArgumentNullException(String name) {
        super("Argument '%s' cannot be null".formatted(name));
    }
}

package tech.grove.onion.implementation.core;

import java.util.logging.Level;

public interface LoggerApi {

    String name();

    boolean isEnabled(Level level);
}

package tech.grove.onion.backend.logger;

import java.util.logging.Level;

public interface LoggerInfo {

    String name();

    boolean isEnabled(Level level);
}

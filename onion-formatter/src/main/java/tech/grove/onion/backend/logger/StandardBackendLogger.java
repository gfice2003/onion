package tech.grove.onion.backend.logger;

import tech.grove.onion.compiler.Compiled;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardBackendLogger implements BackendLogger<String> {

    private final Logger logger;

    public StandardBackendLogger(String name) {
        this.logger = Logger.getLogger(name);
    }

    @Override
    public String name() {
        return logger.getName();
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isLoggable(level);
    }

    @Override
    public void write(Compiled<String> layer) {
        logger.log(layer.level(),layer.data());
    }
}
package tech.grove.onion.compiled;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardCompiledLogger implements CompiledLayerLogger<String> {

    private final Logger logger;

    public StandardCompiledLogger(String name) {
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
    public void write(CompiledLayer<String> layer) {
        logger.log(layer.level(),layer.data());
    }
}
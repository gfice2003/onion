package tech.grove.onion.backend;

import com.google.common.collect.Maps;
import tech.grove.onion.backend.logger.BackendLogger;
import tech.grove.onion.backend.logger.slf4jBackendLogger;
import tech.grove.onion.compiler.Compiled;

import java.util.Map;
import java.util.function.Function;

public class LoggingBackend implements LoggingBackendApi {

    private static Function<String, BackendLogger<String>> LOGGER_FACTORY = slf4jBackendLogger::new;

    private final static Map<String, BackendLogger<String>> LOGGERS = Maps.newConcurrentMap();

    public static void initializeLoggerFactory(Function<String, BackendLogger<String>> factory) {
        LOGGER_FACTORY = factory;
    }

    static void resetLoggers() {
        LOGGERS.clear();
    }

    static void resetLoggerFactory() {
        LOGGER_FACTORY = slf4jBackendLogger::new;
    }

    @Override
    public BackendLogger<String> loggerFor(String name) {
        return LOGGERS.computeIfAbsent(name, LOGGER_FACTORY);
    }

    @Override
    public void write(Compiled<String> string) {
        loggerFor(string.logger()).write(string);
    }
}

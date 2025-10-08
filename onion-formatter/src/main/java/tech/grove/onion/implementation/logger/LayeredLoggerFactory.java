package tech.grove.onion.implementation.logger;

import com.google.common.collect.Maps;
import tech.grove.onion.compiled.CompiledLayerLogger;
import tech.grove.onion.compiled.SystemOutCompiledLogger;

import java.util.Map;
import java.util.function.Function;

public class LayeredLoggerFactory {

    //-- TODO: Move to external config + change default to slf4j
    private static       Function<String, CompiledLayerLogger<String>> compiledLoggerFactory = SystemOutCompiledLogger::new;
    private final static Map<String, LayeredLogger>                    loggers               = Maps.newConcurrentMap();

    public static LayeredLogger getLogger(String logger) {
        return loggers.computeIfAbsent(logger, x -> new LayeredLogger(compiledLoggerFactory.apply(x)));
    }

    public static void initializeCompiledLoggerFactory(Function<String, CompiledLayerLogger<String>> compiledLoggerFactory) {
        LayeredLoggerFactory.compiledLoggerFactory = compiledLoggerFactory;
        loggers.clear();
    }
}

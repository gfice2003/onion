package tech.grove.onion;

import tech.grove.onion.api.Layer;
import tech.grove.onion.api.Log;
import tech.grove.onion.api.LogApi;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.implementation.core.LoggingCore;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.implementation.logger.LayeredLoggerFactory;
import tech.grove.onion.tools.StackHelper;
import tech.grove.onion.tools.StackHelperApi;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class AbstractLog<T extends Layer> implements Log<T> {

    static Function<String, LoggingCoreApi> coreResolver = x -> new LoggingCore(LayeredLoggerFactory.getLogger(x));
    static StackHelperApi                   stack        = new StackHelper().withKnown(AbstractLog.class);

    public static void initializeCoreResolver(Function<String, LoggingCoreApi> coreResolver) {
        AbstractLog.coreResolver = coreResolver;
    }

    protected static LoggingCoreApi coreForLogger(String logger) {

        if (logger == null) {
            throw new ArgumentNullException("logger");
        }

        return coreResolver.apply(logger);
    }

    protected static void addKnownToStackHelper(Class<?>... knownClasses) {
        Arrays.stream(knownClasses).forEach(stack::withKnown);
    }

    private final Class<T>       layerClass;
    private final LoggingCoreApi core;

    protected AbstractLog(String logger, Class<T> layerClass) {
        this.core       = coreForLogger(logger);
        this.layerClass = layerClass;
    }

    @Override
    public T level(Level level) {
        return checkAndCreateLayer(level, null);
    }

    private T dummy() {
        return core.layer(layerClass).dummy();
    }

    private T checkAndCreateLayer(Level level, Predicate<Handle> check) {

        if (!core.isEnabled(level)) {
            return dummy();
        }

        var handle = getHandle();

        if (check != null && !check.test(handle)) {
            return dummy();
        }

        return core.layer(layerClass).create(handle, level);
    }

    private Handle getHandle() {
        return Handle.of(stack.getFirstUnknownFrame());
    }

    @Override
    public LogApi.LevelSetter<T> every(int hits) {
        return level -> checkAndCreateLayer(level, x -> core.isAllowed(x).with(hits));
    }

    @Override
    public LogApi.LevelSetter<T> every(Duration timeout) {
        return level -> checkAndCreateLayer(level, x -> core.isAllowed(x).with(timeout));
    }

    @Override
    public LogApi.LevelSetter<T> every(Supplier<Boolean> predicate) {
        return level -> checkAndCreateLayer(level, x -> predicate.get());
    }
}

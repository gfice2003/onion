package tech.grove.onion.log;

import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.api.log.LevelSetter;
import tech.grove.onion.api.log.Log;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.log.context.LogContext;
import tech.grove.onion.log.context.LogContextApi;
import tech.grove.onion.tools.stack.StackHelper;
import tech.grove.onion.tools.stack.StackHelperApi;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class AbstractLog<T extends Layer> implements Log<T> {

    static StackHelperApi STACK = new StackHelper().withKnown(AbstractLog.class);

    protected static LogContextApi contextForLogger(String logger) {

        if (logger == null) {
            throw new ArgumentNullException("logger");
        }

        return new LogContext(logger);
    }

    protected static void addKnownToStackHelper(Class<?>... knownClasses) {
        Arrays.stream(knownClasses).forEach(STACK::withKnown);
    }

    private final Class<T>      layerClass;
    private final LogContextApi context;

    protected AbstractLog(String logger, Class<T> layerClass) {
        this(contextForLogger(logger), layerClass);
    }

    protected AbstractLog(LogContextApi context, Class<T> layerClass) {
        this.context    = context;
        this.layerClass = layerClass;
    }

    Class<T> layerClass() {
        return layerClass;
    }

    LogContextApi context() {
        return context;
    }

    @Override
    public T level(Level level) {
        return checkAndCreateLayer(level, null);
    }

    private T dummy() {
        return context.layer(layerClass).dummyBuilder();
    }

    private T checkAndCreateLayer(Level level, Predicate<Handle> check) {

        if (!context.isEnabled(level)) {
            return dummy();
        }

        if (check != null && !check.test(getHandle())) {
            return dummy();
        }

        return context.layer(layerClass).createBuilderAt(level);
    }

    private Handle getHandle() {
        return Handle.of(STACK.getFirstUnknownFrame());
    }

    @Override
    public LevelSetter<T> every(int hits) {
        return level -> checkAndCreateLayer(level, x -> context.isAllowed(x).with(hits));
    }

    @Override
    public LevelSetter<T> every(Duration timeout) {
        return level -> checkAndCreateLayer(level, x -> context.isAllowed(x).with(timeout));
    }

    @Override
    public LevelSetter<T> every(Supplier<Boolean> predicate) {
        return level -> checkAndCreateLayer(level, x -> predicate.get());
    }
}

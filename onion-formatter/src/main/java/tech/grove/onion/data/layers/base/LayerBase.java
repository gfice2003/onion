package tech.grove.onion.data.layers.base;

import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.fields.Field;
import tech.grove.onion.data.fields.LayerFields;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.tools.Builder;
import tech.grove.onion.tools.ToStringBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class LayerBase<T extends LayerBase<T>> extends Builder<T> {

    interface Default {
        Level LEVEL = Level.INFO;
    }

    private final Handle            handle;
    private final Supplier<Instant> now;
    private final LoggingCoreApi    core;

    private Level    level;
    private String   pattern;
    private Object[] parameters;
    private Instant  timestamp;

    private int         depth    = 0;
    private LayerFields fields   = null;
    private Duration    duration = null;

    protected LayerBase(LoggingCoreApi core, Handle handle, Level level) {
        this(core, handle, level, Instant::now);
    }

    protected LayerBase(LayerBase<?> source) {
        this(source.core, source.handle, source.level, source.now);

        this.pattern    = source.pattern();
        this.parameters = source.parameters();
        this.fields     = source.fields;
        this.depth      = source.depth;
        this.duration   = source.duration;
        this.timestamp  = source.timestamp;
    }

    protected LayerBase(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {

        this.core   = Optional.ofNullable(core).orElseThrow(() -> new ArgumentNullException("core"));
        this.handle = Optional.ofNullable(handle).orElseThrow(() -> new ArgumentNullException("handle"));
        this.level  = Optional.ofNullable(level).orElse(Default.LEVEL);
        this.now    = now;

        updateTimestamp();
    }

    protected void updateTimestamp() {
        timestamp = now.get();
    }

    public LoggingCoreApi core() {
        return core;
    }

    public abstract Type type();

    public Handle handle() {
        return handle;
    }

    public Level level() {
        return level;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public Duration duration() {
        return duration;
    }

    public String pattern() {
        return pattern;
    }

    public Object[] parameters() {
        return parameters;
    }

    public int depth() {
        return depth;
    }

    protected T registerField(String name, Object value) {

        if (fields == null) {
            fields = new LayerFields();
        }

        return setAndContinue(fields, f -> f.add(name, value));
    }

    public Iterable<Field> fields() {
        return fields;
    }

    public T diveTo(int depth) {
        return setAndContinue(Math.max(0, depth), x -> this.depth = x);
    }

    public T withDuration(Duration duration) {
        return setAndContinue(duration, x -> this.duration = x, NullAction.SKIP);
    }

    public T promoteTo(Level level) {
        return setAndContinue(level, l -> this.level = l, NullAction.SKIP);
    }

    public ParametersSetter<T> format(String pattern) {
        return parameters ->
                setAndContinue(pattern, this::setPattern, NullAction.SKIP)
                        .setAndContinue(parameters, this::setParameters, NullAction.SKIP);
    }

    private void setPattern(String pattern) {
        this.pattern = pattern;
    }

    private void setParameters(Object[] parameters) {
        if (parameters.length > 0) {
            this.parameters = parameters;
        }
    }

    public interface ParametersSetter<T> {
        T with(Object[] parameters);
    }

    protected void commit() {
        core.accept(this);
    }

    public enum Type {
        LAYER,
        IN,
        OUT
    }

    protected void toStringHandler(ToStringBuilder builder) {
        builder.property("Class").set(getClass());
        builder.property("Timestamp").set(timestamp);
        builder.property("Logger").set(core.name());
        builder.property("Level").set(level);
        builder.property("Pattern").set(pattern);
        builder.property("Parameters").set(parameters);

        try (var ignored = builder.withProperty("Handle")) {
            builder.property("ClassName").set(handle().className());
            builder.property("MethodName").set(handle().methodName());
            builder.property("LineNumber").set(handle().lineNumber());
        }
    }

    protected String compileToString() {
        var builder = new ToStringBuilder();
        toStringHandler(builder);
        return builder.toString();
    }


    @Override
    public String toString() {
        return compileToString();
    }
}

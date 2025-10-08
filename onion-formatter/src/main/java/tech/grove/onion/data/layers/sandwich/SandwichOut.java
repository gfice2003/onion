package tech.grove.onion.data.layers.sandwich;

import tech.grove.onion.api.SandwichApi;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.tools.ToStringBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

public class SandwichOut extends LayerBase<SandwichOut> implements SandwichApi.Reporter {

    private boolean    isSkipped = false;
    private SandwichIn source;

    public SandwichOut(SandwichIn source) {
        super(source);
        this.source = source;
    }

    SandwichOut(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        super(core, handle, level, now);
    }

    @Override
    public Type type() {
        return Type.OUT;
    }

    public void skip() {
        isSkipped = true;
    }

    public String name() {
        return Optional.ofNullable(source)
                .map(SandwichIn::name)
                .orElse(null);
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    @Override
    public void report(Level level, String pattern, Object... args) {

        if (level != Level.OFF) {
            promoteTo(level);
        }

        format(pattern).with(args);
    }

    @Override
    public void close() {
        updateTimestampAndSetDuration();
        commit();
    }

    private void updateTimestampAndSetDuration() {

        var previous = timestamp();

        updateTimestamp();
        withDuration(Duration.between(previous, timestamp()));
    }

    @Override
    protected void toStringHandler(ToStringBuilder builder) {
        super.toStringHandler(builder);
        builder.property("Skipped").set(isSkipped);
    }
}

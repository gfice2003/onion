package tech.grove.onion.data.layers.sandwich;

import org.apache.commons.lang3.ArrayUtils;
import tech.grove.onion.api.SandwichApi;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.FieldsLayer;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.layers.AbstractLayer;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.tools.ToStringBuilder;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

public class SandwichIn extends LayerBase<SandwichIn> implements SandwichApi.Adder {

    interface Token {
        String NAME_SEPARATOR = ": ";
        String RESULT         = " Result - ";
    }

    private final String name;

    public SandwichIn(AbstractLayer<?> source, String name) {
        super(source);

        this.name = name;
    }

    SandwichIn(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now, String name) {
        super(core, handle, level, now);

        this.name = name;
    }

    @Override
    public Type type() {
        return Type.IN;
    }

    public String name() {
        return name;
    }

    @Override
    public SandwichApi.Reporter add(String pattern, Object... parameters) {
        format(pattern).with(parameters).commit();
        return new SandwichOut(this);
    }

    @Override
    protected void toStringHandler(ToStringBuilder builder) {
        super.toStringHandler(builder);
        builder.property("Name").set(name);
    }

    public FieldsLayer tryMerge(SandwichOut out) {

        if (out.isSkipped()) {
            return null;
        }

        var result = new FieldsLayer(this);

        var pattern    = mergePattern(out.pattern());
        var parameters = mergeParameters(out.parameters());

        return result.promoteTo(out.level())
                .withDuration(out.duration())
                .format(pattern).with(parameters);
    }

    private String mergePattern(String outPattern) {

        var result = new StringBuilder(name)
                .append(Token.NAME_SEPARATOR);

        if (pattern() != null) {
            result.append(pattern());
        }

        return result
                .append(Token.RESULT)
                .append(outPattern)
                .toString();
    }

    private Object[] mergeParameters(Object[] outParameters) {
        if (parameters() != null && outParameters != null) {
            return ArrayUtils.addAll(parameters(), outParameters);
        } else {
            return Optional.ofNullable(parameters()).orElse(outParameters);
        }
    }
}

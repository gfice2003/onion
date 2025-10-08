package tech.grove.onion.data.layers;

import tech.grove.onion.api.Layer;
import tech.grove.onion.api.LayerApi;
import tech.grove.onion.api.SandwichApi;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.sandwich.SandwichIn;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackInfo;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.tools.Builder;
import tech.grove.onion.tools.StackHelper;
import tech.grove.onion.tools.StackHelperApi;
import tech.grove.onion.tools.ToStringBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class AbstractLayer<T extends AbstractLayer<T>> extends LayerBase<T> implements Layer, LayerApi.DefaultAdder {

    static StackHelperApi STACK_GETTER = new StackHelper().withKnown(AbstractLayer.class).withKnown(LayerApi.StackFormatter.class);

    private StackInfo             stack            = null;
    private ExceptionInfo.Builder exceptionBuilder = null;

    protected AbstractLayer(LayerBase<?> source) {
        super(source);
    }

    protected AbstractLayer(LoggingCoreApi core, Handle handle, Level level) {
        super(core, handle, level);
    }

    protected AbstractLayer(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        super(core, handle, level, now);
    }

    public StackInfo stack() {
        return stack;
    }

    public ExceptionInfo exception() {
        return Optional.ofNullable(exceptionBuilder)
                .map(ExceptionInfo.Builder::build)
                .orElse(null);
    }

    @Override
    public Type type() {
        return Type.LAYER;
    }

    @Override
    public LayerApi.DefaultAdder putStack(StackMode mode) {
        return setAndContinue(mode, m ->
                stack = StackHelper.toInfo(STACK_GETTER.getUnknown())
                        .mode(m));
    }

    @Override
    public SandwichApi.Adder layer(String name) {
        return new SandwichIn(this, name);
    }

    @Override
    public LayerApi.ExceptionFormatter exception(Throwable exception) {
        return setAndContinue(exception, x -> this.exceptionBuilder = ExceptionInfo.of(exception))
                .new ExceptionFormatter();
    }

    @Override
    public void add(String pattern, Object... parameters) {
        format(pattern).with(parameters).commit();
    }

    @Override
    protected T registerField(String name, Object value) {
        return super.registerField(name, value);
    }

    @Override
    protected void toStringHandler(ToStringBuilder builder) {

        super.toStringHandler(builder);

        //-- Fields
        if (fields() != null) {
            try (var ignored = builder.withProperty("Fields")) {
                for (var field : fields()) {
                    builder.property(field.name()).set(field.value());
                }
            }
        } else {
            builder.property("Fields").set("none");
        }

        //-- Stack
        if (stack != null) {
            try (var ignored = builder.withProperty("Stack")) {
                builder.property("Mode").set(stack.mode());
                builder.property("Size").set(stack.size());
                builder.property("Elements").set(stack.elements());
            }
        } else {
            builder.property("Stack").set("none");
        }

        //-- Exception
        var exception = exception();

        if (exception != null) {
            try (var ignored = builder.withProperty("Exception")) {

                builder.property("Class").set(exception.type());
                builder.property("Message").set(exception.message());

                try (var ignored1 = builder.withProperty("Stack")) {
                    builder.property("Mode").set(exception.stack().mode());
                    builder.property("Size").set(exception.stack().size());
                    builder.property("Elements").set(exception.stack().elements());
                }
            }
        } else {
            builder.property("Exception").set("none");
        }
    }

    final class ExceptionFormatter extends Builder<ExceptionFormatter>
            implements LayerApi.ExceptionFormatter {

        @Override
        public LayerApi.ExceptionFormatter stackMode(StackMode mode) {
            return setAndContinue(mode, x -> exceptionBuilder.stackMode(x));
        }

        @Override
        public LayerApi.ExceptionFormatter noMessage() {
            return setAndContinue(null, x -> exceptionBuilder.noMessage());
        }

        @Override
        public LayerApi.ExceptionFormatter noClass() {
            return setAndContinue(null, x -> exceptionBuilder.noClass());
        }

        @Override
        public void add(String pattern, Object... parameters) {
            AbstractLayer.this.add(pattern, parameters);
        }
    }
}

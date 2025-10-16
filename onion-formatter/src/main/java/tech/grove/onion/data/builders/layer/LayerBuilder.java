package tech.grove.onion.data.builders.layer;

import tech.grove.onion.api.common.Stack;
import tech.grove.onion.api.layer.DefaultAdder;
import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.api.layer.LayerExceptionStack;
import tech.grove.onion.api.shell.Adder;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.tools.stack.StackHelper;
import tech.grove.onion.tools.stack.StackHelperApi;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class LayerBuilder<T extends LayerBuilder<T>> extends BaseBuilder<T> implements Layer, LayerExceptionStack, DefaultAdder {

    interface Token {
        String NO_NAME = "NoName";
    }

    static StackHelperApi STACK_GETTER = new StackHelper()
            .withKnown(LayerBuilder.class)
            .withKnown(Stack.class);

    protected LayerBuilder(BaseBuilder<?> source) {
        super(source);
    }

    protected LayerBuilder(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        super(consumer, data);
    }

    @Override
    public Level level() {
        return data.level();
    }

    @Override
    public LayerExceptionStack exception(Throwable exception) {
        return setAndContinue(exception, data::withException, NullAction.SKIP);
    }

    @Override
    public DefaultAdder putStack(StackMode mode) {
        return setAndContinue(STACK_GETTER.getUnknown(), data::withStack, NullAction.SKIP)
                .stackMode(mode);
    }

    @Override
    public DefaultAdder stackMode(StackMode mode) {
        return setAndContinue(mode, data::withStackMode, NullAction.ACCEPT);
    }

    @Override
    public void add(String pattern, Object... parameters) {

        data.withPattern(pattern);
        data.withParameters(parameters);

        commit();
    }

    @Override
    public Adder shell(String name) {

        data.withName(Optional.ofNullable(name)
                              .orElse(Token.NO_NAME));

        return new ShellBuilder(this);
    }
}

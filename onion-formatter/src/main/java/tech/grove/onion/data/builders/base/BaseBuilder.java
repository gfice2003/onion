package tech.grove.onion.data.builders.base;

import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.preprocessors.depth.Diver;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.tools.Builder;

import java.util.Optional;
import java.util.function.Consumer;

public class BaseBuilder<T extends BaseBuilder<T>> extends Builder<T> implements Diver<T> {

    protected final Consumer<? super BaseBuilder<?>> consumer;
    protected final DataCore                         data;

    protected BaseBuilder(BaseBuilder<?> source) {
        this(source.consumer, source.data);
    }

    protected BaseBuilder(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        this.consumer = Optional.ofNullable(consumer)
                .orElseThrow(() -> new ArgumentNullException("consumer"));
        this.data     = Optional.ofNullable(data)
                .orElseThrow(() -> new ArgumentNullException("data"));
    }

    protected void commit() {
        consumer.accept(this);
    }

    public Consumer<? super BaseBuilder<?>> consumer() {
        return consumer;
    }

    public DataCore data() {
        return data;
    }

    @Override
    public T diveTo(int depth) {
        return setAndContinue(depth, data::withDepth);
    }
}

package tech.grove.onion.tools.chained;

import java.util.function.Consumer;

public interface ChainedTransformer<I, O> extends Consumer<I> {
    void bindConsumer(Consumer<O> outputConsumer);
}

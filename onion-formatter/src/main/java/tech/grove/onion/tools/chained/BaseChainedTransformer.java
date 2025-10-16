package tech.grove.onion.tools.chained;

import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.exceptions.ConsumerNotBoundException;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class BaseChainedTransformer<I, O> implements ChainedTransformer<I, O> {

    private Consumer<O> outputConsumer = null;

    @Override
    public void bindConsumer(Consumer<O> outputConsumer) {
        this.outputConsumer = Optional.ofNullable(outputConsumer)
                .orElseThrow(() -> new ArgumentNullException("outputConsumer"));
    }

    @Override
    public void accept(I input) {
        Optional.ofNullable(input)
                .map(this::transform)
                .ifPresent(this::proceed);
    }

    protected void proceed(O output) {
        outputConsumer().accept(output);
    }

    private Consumer<O> outputConsumer() {
        return Optional.ofNullable(outputConsumer)
                .orElseThrow(() -> new ConsumerNotBoundException(this));
    }

    protected abstract O transform(I input);
}

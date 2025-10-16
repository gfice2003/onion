package tech.grove.onion.exceptions;

import tech.grove.onion.tools.chained.ChainedTransformer;

public class ConsumerNotBoundException extends RuntimeException {

    public ConsumerNotBoundException(ChainedTransformer<?,?> transformer) {
        super("Output consumer not bound for '%s'".formatted(transformer));
    }
}

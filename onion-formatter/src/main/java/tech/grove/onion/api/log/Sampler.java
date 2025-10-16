package tech.grove.onion.api.log;

import java.time.Duration;
import java.util.function.Supplier;

public interface Sampler<T> {

    T every(int hits);

    T every(Duration timeout);

    T every(Supplier<Boolean> predicate);
}
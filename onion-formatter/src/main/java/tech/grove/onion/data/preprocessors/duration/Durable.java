package tech.grove.onion.data.preprocessors.duration;

import java.time.Duration;

public interface Durable<T> {

    T lastFor(Duration duration);
}

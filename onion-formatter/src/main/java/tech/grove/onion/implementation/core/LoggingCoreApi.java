package tech.grove.onion.implementation.core;

import tech.grove.onion.api.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.implementation.logger.LayeredLoggerApi;

import java.time.Duration;
import java.util.logging.Level;

public interface LoggingCoreApi extends LayeredLoggerApi {

    PredicateCheck isAllowed(Handle handle);

    interface PredicateCheck {

        boolean with(int count);

        boolean with(Duration timeout);
    }

    <T extends Layer> LayerClassAccessor<T> layer(Class<T> layerClass);

    interface LayerClassAccessor<T extends Layer> {

        T create(Handle handle, Level level);

        T dummy();
    }
}

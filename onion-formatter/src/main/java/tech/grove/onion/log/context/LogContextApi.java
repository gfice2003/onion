package tech.grove.onion.log.context;


import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.backend.logger.LoggerInfo;

import java.time.Duration;
import java.util.logging.Level;

public interface LogContextApi extends LoggerInfo {

    PredicateCheck isAllowed(Handle handle);

    interface PredicateCheck {

        boolean with(int count);

        boolean with(Duration timeout);
    }

    <T extends Layer> LayerBuilderFactory<T> layer(Class<T> layerClass);

    interface LayerBuilderFactory<T extends Layer> {

        T createBuilderAt(Level level);

        T dummyBuilder();
    }
}

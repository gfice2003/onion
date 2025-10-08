package tech.grove.onion.implementation.logger;

import tech.grove.onion.implementation.core.LayerConsumer;
import tech.grove.onion.implementation.core.LoggerApi;

public interface LayeredLoggerApi extends LoggerApi, LayerConsumer {
}

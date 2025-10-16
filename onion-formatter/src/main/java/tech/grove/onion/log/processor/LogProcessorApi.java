package tech.grove.onion.log.processor;

import tech.grove.onion.backend.logger.LoggerInfo;
import tech.grove.onion.data.builders.BuilderConsumer;

public interface LogProcessorApi extends BuilderConsumer {

    LoggerInfo loggerFor(String logger);
}

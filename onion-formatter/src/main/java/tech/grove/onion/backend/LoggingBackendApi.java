package tech.grove.onion.backend;

import tech.grove.onion.backend.logger.LoggerInfo;
import tech.grove.onion.compiler.Compiled;

public interface LoggingBackendApi {

    LoggerInfo loggerFor(String name);

    void write(Compiled<String> string);
}

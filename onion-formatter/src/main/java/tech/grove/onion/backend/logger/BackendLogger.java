package tech.grove.onion.backend.logger;

import tech.grove.onion.compiler.Compiled;

public interface BackendLogger<T> extends LoggerInfo {

    void write(Compiled<T> layer);
}

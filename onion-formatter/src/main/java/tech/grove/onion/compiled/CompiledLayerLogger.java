package tech.grove.onion.compiled;

import tech.grove.onion.implementation.core.LoggerApi;

public interface CompiledLayerLogger<T> extends LoggerApi {

    void write(CompiledLayer<T> layer);
}

package tech.grove.onion.implementation.compilers;

import tech.grove.onion.compiled.CompiledLayer;
import tech.grove.onion.data.layers.base.LayerBase;

public interface LayerCompilerApi<T> {

    CompiledLayer<T> compile(LayerBase<?> layer);
}

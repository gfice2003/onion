package tech.grove.onion.implementation.core;

import tech.grove.onion.data.layers.base.LayerBase;

public interface LayerConsumer {

    void accept(LayerBase<? extends LayerBase<?>> layer);
}

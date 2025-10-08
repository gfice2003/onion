package tech.grove.onion.data.layers;

import tech.grove.onion.api.FieldsLayerApi;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.sandwich.SandwichIn;
import tech.grove.onion.implementation.core.LoggingCoreApi;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.logging.Level;

public class FieldsLayer extends AbstractLayer<FieldsLayer> implements FieldsLayerApi {

    public FieldsLayer(SandwichIn sandwichIn) {
        super(sandwichIn);
    }

    public FieldsLayer(LoggingCoreApi core, Handle handle, Level level) {
        super(core, handle, level);
    }

    FieldsLayer(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        super(core, handle, level, now);
    }

    @Override
    public ValueSetter field(String name) {
        return value -> registerField(name, value);
    }
}

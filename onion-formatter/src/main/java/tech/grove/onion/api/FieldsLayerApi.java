package tech.grove.onion.api;

public interface FieldsLayerApi extends Layer {

    ValueSetter field(String name);

    interface ValueSetter {
        FieldsLayerApi set(Object value);
    }
}

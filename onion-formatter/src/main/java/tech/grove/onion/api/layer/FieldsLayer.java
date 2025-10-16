package tech.grove.onion.api.layer;

public interface FieldsLayer extends Layer {

    ValueSetter field(String name);

    interface ValueSetter {
        DefaultFieldsLayer set(Object value);
    }

    interface DefaultFieldsLayer extends FieldsLayer, DefaultAdder {
    }
}
package tech.grove.onion.api.log;

import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.api.layer.FieldsLayer;

import java.time.Duration;
import java.util.logging.Level;

public interface Log<T extends Layer> extends Sampler<LevelSetter<T>>, LevelSetter<T> {

    static void main(String[] args) {
        Log<FieldsLayer> l = null;

        l.config();
        l.level(Level.ALL);

        l.every(3).config();
        l.every(Duration.ZERO).fine().add("Hello");
        l.every(() -> true).severe().field("s").set(43).add();
    }
}

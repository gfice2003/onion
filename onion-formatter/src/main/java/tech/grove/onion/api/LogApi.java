package tech.grove.onion.api;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.logging.Level;

public interface LogApi {

    interface Sampler<Layer> extends LevelSetter<Layer> {

        LevelSetter<Layer> every(int hits);

        LevelSetter<Layer> every(Duration timeout);

        LevelSetter<Layer> every(Supplier<Boolean> predicate);
    }

    interface LevelSetter<Layer> {

        default Layer finest() {
            return level(Level.FINEST);
        }

        default Layer finer() {
            return level(Level.FINER);
        }

        default Layer fine() {
            return level(Level.FINE);
        }

        default Layer config() {
            return level(Level.CONFIG);
        }

        default Layer info() {
            return level(Level.INFO);
        }

        default Layer warning() {
            return level(Level.WARNING);
        }

        default Layer severe() {
            return level(Level.SEVERE);
        }

        Layer level(Level level);
    }
}

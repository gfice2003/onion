package tech.grove.onion.api.log;

import java.util.logging.Level;

public interface LevelSetter<T> {

    default T finest() {
        return level(Level.FINEST);
    }

    default T finer() {
        return level(Level.FINER);
    }

    default T fine() {
        return level(Level.FINE);
    }

    default T config() {
        return level(Level.CONFIG);
    }

    default T info() {
        return level(Level.INFO);
    }

    default T warning() {
        return level(Level.WARNING);
    }

    default T severe() {
        return level(Level.SEVERE);
    }

    T level(Level level);
}

package tech.grove.onion.compiled;

import java.util.logging.Level;

public record CompiledLayer<T>(Level level, T data) {
}

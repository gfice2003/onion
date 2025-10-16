package tech.grove.onion.compiler;

import java.util.logging.Level;

public record Compiled<T>(String logger, Level level, T data) {
}

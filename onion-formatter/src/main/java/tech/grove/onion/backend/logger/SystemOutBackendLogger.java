package tech.grove.onion.backend.logger;

import tech.grove.onion.compiler.Compiled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class SystemOutBackendLogger implements BackendLogger<String> {

    private static final class Color {
        public static final String RESET  = "\u001B[0m";
        public static final String BLACK  = "\u001B[30m";
        public static final String RED    = "\u001B[31m";
        public static final String GREEN  = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE   = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String CYAN   = "\u001B[36m";
        public static final String WHITE  = "\u001B[37m";
    }

    private static final Map<Level, String> levelColors = Map.of(
            Level.FINEST, Color.CYAN,
            Level.FINER, Color.GREEN,
            Level.FINE, Color.GREEN,
            Level.CONFIG, Color.WHITE,
            Level.INFO, Color.WHITE,
            Level.WARNING, Color.YELLOW,
            Level.SEVERE, Color.RED
    );

    private final String            name;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss.SSS");

    public SystemOutBackendLogger(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isEnabled(Level level) {
        return true;
    }

    @Override
    public void write(Compiled<String> layer) {
        System.out.println(prepareMessage(layer.level(), layer.data()));
    }

    private String colorFor(Level level) {
        return Optional.ofNullable(levelColors.get(level)).orElse(Color.WHITE);
    }

    private String prepareMessage(Level level, String message) {
        return colorFor(level) +
               formatter.format(LocalDateTime.now()) +
               " [" +
               Thread.currentThread().getName() +
               "] " +
               message +
               Color.RESET;
    }
}

package tech.grove.onion.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class slf4jCompiledLogger implements CompiledLayerLogger<String> {

    private final Logger logger;
    private final Mapper mapper = new Mapper();

    public slf4jCompiledLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    @Override
    public void write(CompiledLayer<String> layer) {
        mapper.from(layer.level())
                .action.accept(logger, layer.data());
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isEnabledForLevel(mapper.from(level).level);
    }

    @Override
    public String name() {
        return logger.getName();
    }

    private final static class Mapper {

        private static final Element DEFAULT = new Element(org.slf4j.event.Level.INFO);

        private final Map<Level, Element> elements = Map.of(
                Level.FINEST, new Element(org.slf4j.event.Level.TRACE),
                Level.FINER, new Element(org.slf4j.event.Level.DEBUG),
                Level.FINE, new Element(org.slf4j.event.Level.DEBUG),
                Level.CONFIG, new Element(org.slf4j.event.Level.INFO),
                Level.INFO, new Element(org.slf4j.event.Level.INFO),
                Level.WARNING, new Element(org.slf4j.event.Level.WARN),
                Level.SEVERE, new Element(org.slf4j.event.Level.ERROR)
        );

        public Element from(Level level) {
            return Optional.ofNullable(elements.get(level)).orElse(DEFAULT);
        }

        private record Element(org.slf4j.event.Level level, BiConsumer<Logger, String> action) {
            public Element(org.slf4j.event.Level level) {
                this(level, (l, m) -> l.atLevel(level).log(m));
            }
        }
    }
}

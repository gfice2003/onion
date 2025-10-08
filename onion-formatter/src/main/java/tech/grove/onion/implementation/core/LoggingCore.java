package tech.grove.onion.implementation.core;

import tech.grove.onion.api.FieldsLayerApi;
import tech.grove.onion.api.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.DomainLayer;
import tech.grove.onion.data.layers.FieldsLayer;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.dummy.layer.Dummy;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.implementation.logger.LayeredLoggerApi;
import tech.grove.onion.implementation.sampler.LogEntrySampler;

import java.time.Duration;
import java.util.Optional;
import java.util.logging.Level;

import static tech.grove.onion.tools.Cast.cast;

public class LoggingCore implements LoggingCoreApi {

    private final static LogEntrySampler ENTRY_SAMPLER = new LogEntrySampler();

    private final LayeredLoggerApi logger;

    public LoggingCore(LayeredLoggerApi logger) {
        this.logger = Optional.ofNullable(logger)
                .orElseThrow(() -> new ArgumentNullException("logger"));
    }

    @Override
    public String name() {
        return logger.name();
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isEnabled(level);
    }

    @Override
    public void accept(LayerBase<? extends LayerBase<?>> layer) {
        logger.accept(layer);
    }

    @Override
    public PredicateCheck isAllowed(Handle handle) {
        return new PredicateCheck(handle);
    }

    @Override
    public <T extends Layer> LayerClassAccessor<T> layer(Class<T> layerClass) {
        return new LayerClassAccessor<>(layerClass);
    }

    public record PredicateCheck(Handle handle)
            implements LoggingCoreApi.PredicateCheck {

        @Override
        public boolean with(int count) {
            return ENTRY_SAMPLER.check(handle, count);
        }

        @Override
        public boolean with(Duration timeout) {
            return ENTRY_SAMPLER.check(handle, timeout);
        }
    }

    public final class LayerClassAccessor<T extends Layer>
            implements LoggingCoreApi.LayerClassAccessor<T> {

        private final Class<T> layerClass;

        public LayerClassAccessor(Class<T> layerClass) {
            this.layerClass = layerClass;
        }

        @Override
        public T create(Handle handle, Level level) {
            if (layerClass.equals(FieldsLayerApi.class)) {
                return cast(new FieldsLayer(LoggingCore.this, handle, level));
            } else {
                return new DomainLayer<>(LoggingCore.this, handle, level, layerClass).asProxy();
            }
        }

        @Override
        public T dummy() {
            return Dummy.proxyFor(layerClass);
        }
    }
}

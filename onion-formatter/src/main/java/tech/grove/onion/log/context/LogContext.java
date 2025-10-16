package tech.grove.onion.log.context;

import tech.grove.onion.api.layer.FieldsLayer;
import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.backend.logger.LoggerInfo;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.layer.DomainLayerBuilder;
import tech.grove.onion.data.builders.layer.FieldsLayerBuilder;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.dummy.Dummy;
import tech.grove.onion.log.context.sampler.LogEntrySampler;
import tech.grove.onion.log.processor.LogProcessor;
import tech.grove.onion.log.processor.LogProcessorApi;

import java.time.Duration;
import java.util.logging.Level;

import static tech.grove.onion.tools.Cast.cast;

public class LogContext implements LogContextApi {

    private final static LogProcessorApi PROCESSOR     = new LogProcessor();
    private final static LogEntrySampler ENTRY_SAMPLER = new LogEntrySampler();

    private final LoggerInfo logger;

    public LogContext(String logger) {
        this(PROCESSOR.loggerFor(logger));
    }

    LogContext(LoggerInfo logger) {
        this.logger = logger;
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
    public PredicateCheck isAllowed(Handle handle) {
        return new PredicateCheck(handle);
    }

    @Override
    public <T extends Layer> LayerBuilderFactory<T> layer(Class<T> layerClass) {
        return new LayerBuilderFactory<>(layerClass);
    }

    public record PredicateCheck(Handle handle)
            implements LogContextApi.PredicateCheck {

        @Override
        public boolean with(int count) {
            return ENTRY_SAMPLER.check(handle, count);
        }

        @Override
        public boolean with(Duration timeout) {
            return ENTRY_SAMPLER.check(handle, timeout);
        }
    }

    public final class LayerBuilderFactory<T extends Layer>
            implements LogContextApi.LayerBuilderFactory<T> {

        private final Class<T> layerClass;

        public LayerBuilderFactory(Class<T> layerClass) {
            this.layerClass = layerClass;
        }

        @Override
        public T createBuilderAt(Level level) {
            if (layerClass.equals(FieldsLayer.class)) {
                return cast(new FieldsLayerBuilder(PROCESSOR, dataAt(level)));
            } else {
                return new DomainLayerBuilder<>(PROCESSOR, dataAt(level), layerClass).asProxy();
            }
        }

        private DataCore dataAt(Level level) {
            return new DataCore(name()) {{
                withLevel(level);
            }};
        }

        @Override
        public T dummyBuilder() {
            return Dummy.proxyFor(layerClass);
        }
    }
}

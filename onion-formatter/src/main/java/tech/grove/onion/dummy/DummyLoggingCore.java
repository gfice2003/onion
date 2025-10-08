package tech.grove.onion.dummy;

import com.google.common.collect.Sets;
import tech.grove.onion.api.FieldsLayerApi;
import tech.grove.onion.api.Layer;
import tech.grove.onion.data.layers.DomainLayer;
import tech.grove.onion.data.layers.FieldsLayer;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.dummy.layer.Dummy;
import tech.grove.onion.implementation.core.LoggingCoreApi;

import java.time.Duration;
import java.util.Set;
import java.util.logging.Level;

import static tech.grove.onion.tools.Cast.cast;

public class DummyLoggingCore implements LoggingCoreApi {

    private final String     name;
    private final Set<Level> enabled = Sets.newHashSet(
            Level.FINEST,
            Level.FINER,
            Level.FINE,
            Level.CONFIG,
            Level.INFO,
            Level.WARNING,
            Level.SEVERE);

    public DummyLoggingCore(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isEnabled(Level level) {
        return enabled.contains(level);
    }

    @Override
    public PredicateCheck isAllowed(Handle handle) {
        return new PredicateCheck() {
            @Override
            public boolean with(int count) {
                return true;
            }

            @Override
            public boolean with(Duration timeout) {
                return false;
            }
        };
    }

    @Override
    public <T extends Layer> LayerClassAccessor<T> layer(Class<T> layerClass) {
        return new LayerClassAccessor<>(layerClass);
    }

    @Override
    public void accept(LayerBase<? extends LayerBase<?>> layer) {
        System.out.println(layer);
    }

    public final class LayerClassAccessor<T extends Layer> implements LoggingCoreApi.LayerClassAccessor<T> {

        private final Class<T> layerClass;

        public LayerClassAccessor(Class<T> layerClass) {
            this.layerClass = layerClass;
        }

        @Override
        public T create(Handle handle, Level level) {
            if (layerClass.equals(FieldsLayerApi.class)) {
                return cast(new FieldsLayer(DummyLoggingCore.this, handle, level));
            } else {
                return new DomainLayer<>(DummyLoggingCore.this, handle, level, layerClass).asProxy();
            }
        }

        @Override
        public T dummy() {
            return getDummy();
        }

        private T getDummy() {
            return Dummy.proxyFor(layerClass);
        }
    }
}

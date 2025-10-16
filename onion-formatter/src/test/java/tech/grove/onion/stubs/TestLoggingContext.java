package tech.grove.onion.stubs;

import com.google.common.collect.Sets;
import tech.grove.onion.api.layer.FieldsLayer;
import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.BuilderConsumer;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.layer.DomainLayerBuilder;
import tech.grove.onion.data.builders.layer.FieldsLayerBuilder;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.dummy.Dummy;
import tech.grove.onion.log.context.LogContextApi;

import java.time.Duration;
import java.util.Set;
import java.util.logging.Level;

import static tech.grove.onion.tools.Cast.cast;

public class TestLoggingContext implements LogContextApi, BuilderConsumer {

    private final String      name;
    private final Set<Level>  disabled   = Sets.newHashSet();
    private final Set<Handle> disallowed = Sets.newHashSet();

    public TestLoggingContext(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isEnabled(Level level) {
        return !disabled.contains(level);
    }

    @Override
    public PredicateCheck isAllowed(Handle handle) {
        return new PredicateCheck() {
            @Override
            public boolean with(int count) {
                return !disallowed.contains(handle);
            }

            @Override
            public boolean with(Duration timeout) {
                return disallowed.contains(handle);
            }
        };
    }

    @Override
    public <T extends Layer> LayerBuilderFactory<T> layer(Class<T> layerClass) {
        return new LayerBuilderFactory<>(layerClass);
    }

    @Override
    public void accept(BaseBuilder<?> root) {
        System.out.println(root);
    }

    public void disallow(Handle handle) {
        disallowed.add(handle);
    }

    public void disable(Level level) {
        disabled.add(level);
    }

    public void reset() {
        disabled.clear();
        disallowed.clear();
    }

    public final class LayerBuilderFactory<T extends Layer> implements LogContextApi.LayerBuilderFactory<T> {

        private final Class<T> layerClass;

        public LayerBuilderFactory(Class<T> layerClass) {
            this.layerClass = layerClass;
        }

        @Override
        public T createBuilderAt(Level level) {
            if (layerClass.equals(FieldsLayer.class)) {
                return cast(new FieldsLayerBuilder(TestLoggingContext.this, dataAt(level)));
            } else {
                return new DomainLayerBuilder<>(TestLoggingContext.this, dataAt(level), layerClass).asProxy();
            }
        }


        private DataCore dataAt(Level level) {
            return new DataCore(name) {{
                withLevel(level);
            }};
        }

        @Override
        public T dummyBuilder() {
            return getDummy();
        }

        private T getDummy() {
            return Dummy.proxyFor(layerClass);
        }
    }
}

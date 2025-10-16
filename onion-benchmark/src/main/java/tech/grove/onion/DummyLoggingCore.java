//package tech.grove.onion;
//
//import tech.grove.onion.api.FieldsLayerApi;
//import tech.grove.onion.api.Layer;
//import tech.grove.onion.data.context.Handle;
//import tech.grove.onion.data.entries.layers.DomainLayerData;
//import tech.grove.onion.data.entries.FieldsLayer;
//import tech.grove.onion.data.entries.base.LayerBase;
//import tech.grove.onion.dummy.Dummy;
//import tech.grove.onion.implementation.core.LoggingCoreApi;
//
//import java.time.Duration;
//import java.util.logging.Level;
//
//import static tech.grove.onion.tools.Cast.cast;
//
//public class DummyLoggingCore implements LoggingCoreApi {
//
//    private final String  name;
//    private final boolean enabled;
//    private final boolean allowed;
//
//    private final PredicateCheck predicateCheck = new PredicateCheck();
//
//    public DummyLoggingCore(String name, boolean enabled, boolean allowed) {
//        this.name    = name;
//        this.enabled = enabled;
//        this.allowed = allowed;
//    }
//
//    @Override
//    public String name() {
//        return name;
//    }
//
//    @Override
//    public boolean isEnabled(Level level) {
//        return enabled;
//    }
//
//    @Override
//    public PredicateCheck isAllowed(Handle handle) {
//        return predicateCheck;
//    }
//
//    @Override
//    public <T extends Layer> LayerClassAccessor<T> layer(Class<T> layerClass) {
//        return new LayerClassAccessor<>(layerClass);
//    }
//
//    @Override
//    public void accept(LayerBase<?> layer) {
//        //-- NOP
//    }
//
//    public final class PredicateCheck implements LoggingCoreApi.PredicateCheck {
//
//        @Override
//        public boolean with(int count) {
//            return allowed;
//        }
//
//        @Override
//        public boolean with(Duration timeout) {
//            return allowed;
//        }
//    }
//
//    public final class LayerClassAccessor<T extends Layer>
//            implements LoggingCoreApi.LayerClassAccessor<T> {
//
//        private final Class<T> layerClass;
//
//        public LayerClassAccessor(Class<T> layerClass) {
//            this.layerClass = layerClass;
//        }
//
//        @Override
//        public T create(Level level) {
//            if (layerClass.equals(FieldsLayerApi.class)) {
//                return cast(new FieldsLayer(DummyLoggingCore.this, level));
//            } else {
//                return new DomainLayerData<>(DummyLoggingCore.this, level, layerClass).asProxy();
//            }
//        }
//
//        @Override
//        public T dummy() {
//            return Dummy.proxyFor(layerClass);
//        }
//    }
//
//}

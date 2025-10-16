package tech.grove.onion.log;

import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.log.context.LogContextApi;

public class DomainLogTest extends AbstractLogTest<DomainLogTest.DomainLayer, DomainLog<DomainLogTest.DomainLayer>> {

    @Override
    protected Class<DomainLayer> layerClass() {
        return DomainLayer.class;
    }

    @Override
    protected DomainLog<DomainLayer> createTargetForCurrentClass() {
        return DomainLog.within(DomainLayer.class).forCurrentClass();
    }

    @Override
    protected DomainLog<DomainLayer> createTargetForClass(Class<?> logger) {
        return DomainLog.within(DomainLayer.class).forClass(logger);
    }

    @Override
    protected DomainLog<DomainLayer> createTargetForName(String logger) {
        return DomainLog.within(DomainLayer.class).forName(logger);
    }

    @Override
    protected DomainLog<DomainLayer> createTarget(LogContextApi context) {
        return new DomainLog<>(context, DomainLayer.class);
    }

    public interface DomainLayer extends Layer {

    }
}

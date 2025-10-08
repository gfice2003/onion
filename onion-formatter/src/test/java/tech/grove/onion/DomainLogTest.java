package tech.grove.onion;

import tech.grove.onion.api.Layer;

public class DomainLogTest extends AbstractLogTest<DomainLogTest.DomainLayer, DomainLog<DomainLogTest.DomainLayer>> {

    @Override
    protected Class<DomainLayer> layerClass() {
        return DomainLayer.class;
    }

    @Override
    protected DomainLog<DomainLayer> loggerForCurrentClass(Class<DomainLayer> layerClass) {
        return DomainLog.within(DomainLayer.class).forCurrentClass();
    }

    @Override
    protected DomainLog<DomainLayer> loggerForClass(Class<DomainLayer> layerClass) {
        return DomainLog.within(DomainLayer.class).forClass(Data.LOGGER_CLASS);
    }

    @Override
    protected DomainLog<DomainLayer> loggerForName(Class<DomainLayer> layerClass) {
        return DomainLog.within(DomainLayer.class).forClass(Data.LOGGER_CLASS);
    }

    public interface DomainLayer extends Layer {

    }
}

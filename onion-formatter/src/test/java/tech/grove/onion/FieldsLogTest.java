package tech.grove.onion;

import tech.grove.onion.api.FieldsLayerApi;

public class FieldsLogTest extends AbstractLogTest<FieldsLayerApi, FieldsLog> {
    @Override
    protected Class<FieldsLayerApi> layerClass() {
        return FieldsLayerApi.class;
    }

    @Override
    protected FieldsLog loggerForName(Class<FieldsLayerApi> ignored) {
        return FieldsLog.forClass(Data.LOGGER_NAME);
    }

    @Override
    protected FieldsLog loggerForClass(Class<FieldsLayerApi> ignored) {
        return FieldsLog.forClass(Data.LOGGER_CLASS);
    }

    @Override
    protected FieldsLog loggerForCurrentClass(Class<FieldsLayerApi> ignored) {
        return FieldsLog.forCurrentClass();
    }
}

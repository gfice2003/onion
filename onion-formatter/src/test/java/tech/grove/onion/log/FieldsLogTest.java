package tech.grove.onion.log;

import tech.grove.onion.api.layer.FieldsLayer;
import tech.grove.onion.log.context.LogContextApi;

public class FieldsLogTest extends AbstractLogTest<FieldsLayer, FieldsLog> {

    @Override
    protected Class<FieldsLayer> layerClass() {
        return FieldsLayer.class;
    }

    @Override
    protected FieldsLog createTargetForCurrentClass() {
        return FieldsLog.forCurrentClass();
    }

    @Override
    protected FieldsLog createTargetForClass(Class<?> logger) {
        return FieldsLog.forClass(logger);
    }

    @Override
    protected FieldsLog createTargetForName(String logger) {
        return FieldsLog.forName(logger);
    }

    @Override
    protected FieldsLog createTarget(LogContextApi context) {
        return new FieldsLog(context);
    }
}

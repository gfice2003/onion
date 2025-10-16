package tech.grove.onion.log;

import tech.grove.onion.api.layer.FieldsLayer;
import tech.grove.onion.log.context.LogContextApi;

public class FieldsLog extends AbstractLog<FieldsLayer> {

    static {
        addKnownToStackHelper(FieldsLog.class);
    }

    protected FieldsLog(String logger) {
        super(logger, FieldsLayer.class);
    }

    protected FieldsLog(LogContextApi context) {
        super(context, FieldsLayer.class);
    }

    public static FieldsLog forCurrentClass() {
        return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    public static FieldsLog forClass(Class<?> loggerClass) {
        return forName(loggerClass.getName());
    }

    public static FieldsLog forName(String loggerClass) {
        return new FieldsLog(loggerClass);
    }
}
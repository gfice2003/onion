package tech.grove.onion;

import tech.grove.onion.api.FieldsLayerApi;

public class FieldsLog extends AbstractLog<FieldsLayerApi> {

    static {
        addKnownToStackHelper(FieldsLog.class);
    }

    protected FieldsLog(String logger) {
        super(logger, FieldsLayerApi.class);
    }

    public static FieldsLog forCurrentClass() {
        return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
    }

    public static FieldsLog forClass(Class<?> loggerClass) {
        return forClass(loggerClass.getName());
    }

    public static FieldsLog forClass(String loggerClass) {
        return new FieldsLog(loggerClass);
    }
}
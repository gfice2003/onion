package tech.grove.onion;

import tech.grove.onion.api.Layer;

public class DomainLog<L extends Layer> extends AbstractLog<L> {

    static {
        addKnownToStackHelper(DomainLog.class, LoggerClassSetter.class);
    }

    protected DomainLog(String logger, Class<L> layerCLass) {
        super(logger, layerCLass);
    }

    public static <L extends Layer> LoggerClassSetter<L> within(Class<L> domainClass) {
        return loggerClass -> new DomainLog<>(loggerClass, domainClass);
    }

    public interface LoggerClassSetter<L extends Layer> {

        default DomainLog<L> forCurrentClass() {
            return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
        }

        default DomainLog<L> forClass(Class<?> loggerClass) {
            return forClass(loggerClass.getName());
        }

        DomainLog<L> forClass(String loggerClass);
    }
}
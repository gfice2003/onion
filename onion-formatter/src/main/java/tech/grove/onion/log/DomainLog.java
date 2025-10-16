package tech.grove.onion.log;

import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.log.context.LogContextApi;

public class DomainLog<L extends Layer> extends AbstractLog<L> {

    static {
        addKnownToStackHelper(DomainLog.class, LoggerClassSetter.class);
    }

    protected DomainLog(String logger, Class<L> layerCLass) {
        super(logger, layerCLass);
    }

    protected DomainLog(LogContextApi context, Class<L> layerCLass) {
        super(context, layerCLass);
    }

    public static <L extends Layer> LoggerClassSetter<L> within(Class<L> domainClass) {
        return loggerClass -> new DomainLog<>(loggerClass, domainClass);
    }

    public interface LoggerClassSetter<L extends Layer> {

        default DomainLog<L> forCurrentClass() {
            return forClass(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());
        }

        default DomainLog<L> forClass(Class<?> loggerClass) {
            return forName(loggerClass.getName());
        }

        DomainLog<L> forName(String loggerClass);
    }
}
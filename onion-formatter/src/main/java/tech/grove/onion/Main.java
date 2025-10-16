package tech.grove.onion;

import com.google.common.collect.Maps;
import tech.grove.onion.backend.LoggingBackend;
import tech.grove.onion.backend.logger.SystemOutBackendLogger;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.log.FieldsLog;
import tech.grove.onion.log.processor.LogProcessor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {

    private final static FieldsLog log;

    static {
        LoggingBackend.initializeLoggerFactory(SystemOutBackendLogger::new);
        log = FieldsLog.forCurrentClass();
    }

    public static void main(String[] args) {

        System.out.println("[Just message]");
        log.fine().add("Message");

        System.out.println("[Message with parameters]");
        log.fine().add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Stack + message with parameters]");
        log.info().putStack().add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Stack]");
        log.warning().putStack().add();

        System.out.println("[Exception + message with parameters]");
        log.severe().exception(new RuntimeException("Some exception")).add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Exception full stack + message with parameters]");
        log.severe().exception(new RuntimeException("Some exception")).fullStack().add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Exception no stack + message with parameters]");
        log.finest().exception(new RuntimeException("Some exception")).noStack().add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Exception]");
        log.severe().exception(new RuntimeException("Some exception")).add();

        System.out.println("[Field + message with parameters]");
        log.severe().field("one").set(1).add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Fields + message with parameters]");
        log.finer().field("one").set(UUID.randomUUID()).field("two").set(Duration.ofSeconds(2)).add("Message with %s at %s", 4, LocalDateTime.now());

        System.out.println("[Layer top with name]");
        try(var layer = log.config().shell("test").add()){

            System.out.println("[Layer bottom report null]");
            layer.report(null);
        }

        System.out.println("[Layer top for method]");
        try(var layer = log.config().methodShell().add()){

            log.warning().add("In-layer warning message");

            System.out.println("[Layer bottom report new Object()]");
            layer.report(new Object());
        }
    }

}

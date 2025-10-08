package tech.grove.onion;

import org.slf4j.LoggerFactory;
import tech.grove.onion.data.stack.StackMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private final static FieldsLog log = FieldsLog.forCurrentClass();

    public static void main(String[] args) {


        log.finer().putStack().add();

        log.finer().putStack(StackMode.FAIR).add();


//        System.out.println("[Just message]");
//        log.fine().add("Message");
//
//        System.out.println("[Message with parameters]");
//        log.fine().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Stack + message with parameters]");
//        log.info().putStack().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Stack]");
//        log.warning().putStack().add();
//
//        System.out.println("[Exception + message with parameters]");
//        log.severe().exception(new RuntimeException("Some exception")).add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception full stack + message with parameters]");
//        log.severe().exception(new RuntimeException("Some exception")).fullStack().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception no stack + message with parameters]");
//        log.finest().exception(new RuntimeException("Some exception")).noStack().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception no class + message with parameters]");
//        log.config().exception(new RuntimeException("Some exception")).noClass().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception no message + message with parameters]");
//        log.info().exception(new RuntimeException("Some exception")).noMessage().add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Exception]");
//        log.severe().exception(new RuntimeException("Some exception")).add();
//
//        System.out.println("[Field + message with parameters]");
//        log.severe().field("one").set(1).add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Fields + message with parameters]");
//        log.finer().field("one").set(UUID.randomUUID()).field("two").set(Duration.ofSeconds(2)).add("Message with %s at %s", 4, LocalDateTime.now());
//
//        System.out.println("[Layer top with name]");
//        try(var layer = log.config().layer("test").add()){
//
//            System.out.println("[Layer bottom report null]");
//            layer.report(null);
//        }
//
//        System.out.println("[Layer top for method]");
//        try(var layer = log.config().methodLayer().add()){
//
//            log.warning().add("In-layer warning message");
//
//            System.out.println("[Layer bottom report new Object()]");
//            layer.report(new Object());
//        }
    }

}

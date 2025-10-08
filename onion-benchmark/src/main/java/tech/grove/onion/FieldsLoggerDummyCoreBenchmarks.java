package tech.grove.onion;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

//@State(Scope.Benchmark)
//@Fork(value = 1, warmups = 1)
public class FieldsLoggerDummyCoreBenchmarks {
//
//    private FieldsLog     log;
//    private Exception     exception;
//    private LocalDateTime now;
//    private UUID          uid;
//    private Duration      duration;
//    private Object        result;
//
//    public static void main(String[] args) throws IOException {
//        org.openjdk.jmh.Main.main(args);
//    }
//
//    @Setup(Level.Invocation)
//    public void setUp() {
//        AbstractLog.initializeCoreResolver(name -> new DummyLoggingCore(name, false, false));
//
//        log       = FieldsLog.forCurrentClass();
//        exception = new RuntimeException("Some exception");
//        now       = LocalDateTime.now();
//        uid       = UUID.randomUUID();
//        duration  = Duration.ofSeconds(4);
//        result    = new Object();
//    }
//
//    @Benchmark
//    public void justMessage() {
//        log.fine().add("Message");
//    }
//
//    @Benchmark
//    public void messageWithParameters() {
//        log.fine().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void stack() {
//        log.warning().putStack().add();
//    }
//
//    @Benchmark
//    public void stackPlusMessageWithParameters() {
//        log.info().putStack().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void exceptionPlusMessageWithParameters() {
//        log.severe().exception(exception).add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void exceptionFullStackPlusMessageWithParameters() {
//        log.severe().exception(exception).fullStack().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void exceptionNoStackPlusMessageWithParameters() {
//        log.finest().exception(exception).noStack().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void exceptionNoClassPlusMessageWithParameters() {
//        log.finest().exception(exception).noClass().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void exceptionNoMessagePlusMessageWithParameters() {
//        log.finest().exception(exception).noMessage().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void exception() {
//        log.finest().exception(exception).add();
//    }
//
//    @Benchmark
//    public void fieldPlusMessageWithParameters() {
//        log.severe().field("one").set(uid).add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void fieldsPlusMessageWithParameters() {
//        log.finer().field("one").set(uid).field("two").set(duration).add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void layerWithName() {
//        log.finer().layer("layer").add();
//    }
//
//    @Benchmark
//    public void layerWithNameMessageWithParameters() {
//        log.finer().layer("layer").add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void methodLayer() {
//        log.finer().methodLayer().add();
//    }
//
//    @Benchmark
//    public void methodLayerMessageWithParameters() {
//        log.finer().methodLayer().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void flatSandwichNoInMessageNoOutMessage() {
//        try (var layer = log.finer().methodLayer().add()) {
//        }
//    }
//
//    @Benchmark
//    public void flatSandwichWithInMessageNoOutMessage() {
//        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
//        }
//    }
//
//    @Benchmark
//    public void flatSandwichNoInMessageWithOutMessage() {
//        try (var layer = log.finer().methodLayer().add()) {
//            layer.report("Message with %s at %s", 4, now);
//        }
//    }
//
//    @Benchmark
//    public void flatSandwichWithInMessageWithOutMessage() {
//        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
//            layer.report("Message with %s at %s", 4, now);
//        }
//    }
//
//    @Benchmark
//    public void flatSandwichNoInMessageWithResult() {
//        try (var layer = log.finer().methodLayer().add()) {
//            layer.report(result);
//        }
//    }
//
//    @Benchmark
//    public void flatSandwichWithInMessageWithResult() {
//        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
//            layer.report(result);
//        }
//    }
}

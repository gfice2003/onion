package tech.grove.onion;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.data.builders.layer.FieldsLayerBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 3000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
public class DomainLoggerBenchmarks {

    interface Domain extends Layer {

        Domain withUid(UUID value);

        Domain withDuration(Duration value);
    }

    private Exception          exception;
    private LocalDateTime      now;
    private UUID               uid;
    private Duration           duration;
    private Object             result;
    private FieldsLayerBuilder builder;

    @State(Scope.Benchmark)
    public static class Params {

        String        message    = "Hello with parameters %s, %s";
        UUID          parameterA = UUID.randomUUID();
        LocalDateTime parameterB = LocalDateTime.now();
        String        field      = "field";
        Object        value      = BigDecimal.valueOf(23423);
    }

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup(Level.Invocation)
    public void setUp() {

//        AbstractLog.initializeCoreResolver(name -> new DummyLoggingCore(name, true, false));

        builder   = new FieldsLayerBuilder(x -> {
        }, java.util.logging.Level.INFO);
        exception = new RuntimeException("Some exception");
        now       = LocalDateTime.now();
        uid       = UUID.randomUUID();
        duration  = Duration.ofSeconds(4);
        result    = new Object();
    }


    @Benchmark
    public void justMessage(Blackhole blackhole, Params p) {
        builder.add(p.message);
        blackhole.consume(builder.data());
    }

    @Benchmark
    public void messageWithParameters(Blackhole blackhole, Params p) {
        builder.add(p.message, p.parameterA, p.parameterB);
        blackhole.consume(builder.data());
    }

    @Benchmark
    public void justField(Blackhole blackhole, Params p) {
        builder.field(p.field).set(p.value).add();
        blackhole.consume(builder.data());
    }

    @Benchmark
    public void fieldWithMessage(Blackhole blackhole, Params p) {
        builder.field(p.field).set(p.value).add(p.message);
        blackhole.consume(builder.data());
    }

    @Benchmark
    public void fieldWithMessageAndParameters(Blackhole blackhole, Params p) {
        builder.field(p.field).set(p.value).add(p.message, p.parameterA, p.parameterB);
        blackhole.consume(builder.data());
    }

    //
//    @Benchmark
//    public void loggerCreate(Blackhole blackhole) {
//        blackhole.consume(log.config());
//    }
//
//    @Benchmark
//    public void varargsMethodCall(Blackhole blackhole) {
//        blackhole.consume(varargsFormat("Message with %s at %s", 4, now));
//    }

//    private String varargsFormat(String pattern, Object... parameters) {
//        return pattern.formatted(parameters);
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

//    @Benchmark
//    public void fieldPlusMessageWithParameters() {
//        log.severe().withUid(uid).add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void fieldsPlusMessageWithParameters() {
//        log.finer().withUid(uid).withDuration(duration).add("Message with %s at %s", 4, now);
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
//    public void methodShell() {
//        log.finer().methodShell().add();
//    }
//
//    @Benchmark
//    public void firstStackFrame(Blackhole blackhole) {
//        blackhole.consume(STACK.getFirstUnknownFrame().getMethodName());
//    }

//    @Benchmark
//    public void methodShellMessageWithParameters() {
//        log.finer().methodLayer().add("Message with %s at %s", 4, now);
//    }
//
//    @Benchmark
//    public void flatShellNoInMessageNoOutMessage() {
//        try (var layer = log.finer().methodLayer().add()) {
//        }
//    }
//
//    @Benchmark
//    public void flatShellWithInMessageNoOutMessage() {
//        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
//        }
//    }
//
//    @Benchmark
//    public void flatShellNoInMessageWithOutMessage() {
//        try (var layer = log.finer().methodLayer().add()) {
//            layer.report("Message with %s at %s", 4, now);
//        }
//    }
//
//    @Benchmark
//    public void flatShellWithInMessageWithOutMessage() {
//        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
//            layer.report("Message with %s at %s", 4, now);
//        }
//    }
//
//    @Benchmark
//    public void flatShellNoInMessageWithResult() {
//        try (var layer = log.finer().methodLayer().add()) {
//            layer.report(result);
//        }
//    }
//
//    @Benchmark
//    public void flatShellWithInMessageWithResult() {
//        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
//            layer.report(result);
//        }
//    }
}

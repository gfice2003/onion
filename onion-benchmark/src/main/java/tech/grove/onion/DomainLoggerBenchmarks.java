package tech.grove.onion;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import tech.grove.onion.api.Layer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
public class DomainLoggerBenchmarks {

    interface Domain extends Layer {

        Domain withUid(UUID value);

        Domain withDuration(Duration value);
    }

    private DomainLog<Domain> log;
    private Exception         exception;
    private LocalDateTime     now;
    private UUID              uid;
    private Duration          duration;
    private Object            result;
    private Domain            domain;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup(Level.Trial)
    public void setUp() {

        AbstractLog.initializeCoreResolver(name -> new DummyLoggingCore(name, true, false));

        log       = DomainLog.within(Domain.class).forCurrentClass();
        exception = new RuntimeException("Some exception");
        now       = LocalDateTime.now();
        uid       = UUID.randomUUID();
        duration  = Duration.ofSeconds(4);
        result    = new Object();
        domain    = log.info();
    }

    @Benchmark
    public void loggerCreate(Blackhole blackhole) {
        blackhole.consume(log.config());
    }

    @Benchmark
    public void varargsMethodCall(Blackhole blackhole) {
        domain.withUid(uid).withDuration(duration).add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void justMessage() {
        log.fine().add("Message");
    }

    @Benchmark
    public void messageWithParameters() {
        log.fine().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void stack() {
        log.warning().putStack().add();
    }

    @Benchmark
    public void stackPlusMessageWithParameters() {
        log.info().putStack().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void exceptionPlusMessageWithParameters() {
        log.severe().exception(exception).add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void exceptionFullStackPlusMessageWithParameters() {
        log.severe().exception(exception).fullStack().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void exceptionNoStackPlusMessageWithParameters() {
        log.finest().exception(exception).noStack().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void exceptionNoClassPlusMessageWithParameters() {
        log.finest().exception(exception).noClass().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void exceptionNoMessagePlusMessageWithParameters() {
        log.finest().exception(exception).noMessage().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void exception() {
        log.finest().exception(exception).add();
    }

    @Benchmark
    public void fieldPlusMessageWithParameters() {
        log.severe().withUid(uid).add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void fieldsPlusMessageWithParameters() {
        log.finer().withUid(uid).withDuration(duration).add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void layerWithName() {
        log.finer().layer("layer").add();
    }

    @Benchmark
    public void layerWithNameMessageWithParameters() {
        log.finer().layer("layer").add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void methodLayer() {
        log.finer().methodLayer().add();
    }

    @Benchmark
    public void methodLayerMessageWithParameters() {
        log.finer().methodLayer().add("Message with %s at %s", 4, now);
    }

    @Benchmark
    public void flatSandwichNoInMessageNoOutMessage() {
        try (var layer = log.finer().methodLayer().add()) {
        }
    }

    @Benchmark
    public void flatSandwichWithInMessageNoOutMessage() {
        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
        }
    }

    @Benchmark
    public void flatSandwichNoInMessageWithOutMessage() {
        try (var layer = log.finer().methodLayer().add()) {
            layer.report("Message with %s at %s", 4, now);
        }
    }

    @Benchmark
    public void flatSandwichWithInMessageWithOutMessage() {
        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
            layer.report("Message with %s at %s", 4, now);
        }
    }

    @Benchmark
    public void flatSandwichNoInMessageWithResult() {
        try (var layer = log.finer().methodLayer().add()) {
            layer.report(result);
        }
    }

    @Benchmark
    public void flatSandwichWithInMessageWithResult() {
        try (var layer = log.finer().methodLayer().add("Message with %s at %s", 4, now)) {
            layer.report(result);
        }
    }
}

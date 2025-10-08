package tech.grove.onion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.grove.onion.api.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.utils.TestStackHelper;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.AbstractLogTest.Data.*;

public abstract class AbstractLogTest<T extends Layer, L extends AbstractLog<T>> {

    interface Data {
        StackWalker.StackFrame[] STACK        = Arrays.stream(Thread.currentThread().getStackTrace()).map(StackFrame::new).toArray(StackWalker.StackFrame[]::new);
        String                   LOGGER_NAME  = STACK[0].getClassName();
        Class<?>                 LOGGER_CLASS = classForName(LOGGER_NAME);
        StackWalker.StackFrame   STACK_FRAME  = STACK[0];
        Handle                   HANDLE       = Handle.of(STACK_FRAME);

        static Class<?> classForName(String name) {
            try {
                return Class.forName(LOGGER_NAME);
            } catch (Throwable ex) {
                throw new RuntimeException();
            }
        }
    }

    private Core core;
    private L    target;

    @BeforeAll
    static void initializeClass() {
        AbstractLog.stack = new TestStackHelper(STACK);
    }

    @BeforeEach
    public void initializeTest() {
        AbstractLog.coreResolver = x -> core = new Core(HANDLE.className());
    }

    protected abstract Class<T> layerClass();

    protected abstract L loggerForCurrentClass(Class<T> layerClass);

    protected abstract L loggerForClass(Class<T> layerClass);

    protected abstract L loggerForName(Class<T> layerClass);

    @Test
    public void ctor_forCurrentClass_requestsCoreForCorrectClass() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
        }
        then:
        {
            assertSame(HANDLE.className(), core.name());
        }
    }

    @Test
    public void ctor_forLoggerClass_requestsCoreForCorrectClass() {
        given:
        {
            target = loggerForClass(layerClass());
        }
        then:
        {
            assertSame(LOGGER_NAME, core.name());
        }
    }

    @Test
    public void ctor_forLoggerName_requestsCoreForCorrectClass() {
        given:
        {
            target = loggerForName(layerClass());
        }
        then:
        {
            assertSame(LOGGER_NAME, core.name());
        }
    }

    @Test
    public void layer_disabled_queriesForDummy() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
        }
        when:
        {
            target.level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedDummy<>(layerClass()));
        }
    }

    @Test
    public void layer_enabled_queriesForLayerInstance() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
            core.enable(Level.FINE);
        }
        when:
        {
            target.level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedLayer<>(layerClass(), HANDLE, Level.FINE));
        }
    }

    @Test
    public void layer_enabledAndDisabled_queriesForLayerCorrectly() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
            core.enable(Level.FINE);
        }
        when:
        {
            target.level(Level.FINE);
            target.level(Level.CONFIG);
        }
        then:
        {
            assertCreated(
                    new Core.CreatedLayer<>(layerClass(), HANDLE, Level.FINE),
                    new Core.CreatedDummy<>(layerClass()));
        }
    }

    @Test
    public void layer_notAllowed_queriesForDummy() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
            core.enable(Level.FINE);
        }
        when:
        {
            target.every(0).level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedDummy<>(layerClass()));
        }
    }

    @Test
    public void layer_allowed_queriesForLayerInstance() {
        given:
        {
            target = loggerForCurrentClass(layerClass());

            core.allow(HANDLE);
            core.enable(Level.FINE);
        }
        when:
        {
            target.every(0).level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedLayer<>(layerClass(), HANDLE, Level.FINE));
        }
    }

    @Test
    public void layer_allowedAndNotAllowed_queriesForLayerCorrectly() {
        given:
        {
            target = loggerForCurrentClass(layerClass());

            core.allow(HANDLE);
            core.enable(Level.FINE);
        }
        when:
        {
            target.every(0).level(Level.FINE);
            core.disallow(HANDLE);
            target.every(0).level(Level.FINE);
        }
        then:
        {
            assertCreated(
                    new Core.CreatedLayer<>(layerClass(), HANDLE, Level.FINE),
                    new Core.CreatedDummy<>(layerClass()));
        }
    }


    @Test
    public void layer_notAllowedByPredicate_queriesForDummy() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
            core.enable(Level.FINE);
        }
        when:
        {
            target.every(() -> false).level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedDummy<>(layerClass()));
        }
    }

    @Test
    public void layer_allowedByPredicate_queriesForLayerInstance() {
        given:
        {
            target = loggerForCurrentClass(layerClass());

            core.enable(Level.FINE);
        }
        when:
        {
            target.every(() -> true).level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedLayer<>(layerClass(), HANDLE, Level.FINE));
        }
    }

    @Test
    public void layer_allowedByPredicateAndNotAllowed_queriesForLayerCorrectly() {
        given:
        {
            target = loggerForCurrentClass(layerClass());

            core.enable(Level.FINE);
        }
        when:
        {
            target.every(() -> true).level(Level.FINE);
            target.every(() -> false).level(Level.FINE);
        }
        then:
        {
            assertCreated(
                    new Core.CreatedLayer<>(layerClass(), HANDLE, Level.FINE),
                    new Core.CreatedDummy<>(layerClass()));
        }
    }

    @Test
    public void layer_allowedButDisables_queriesForDummy() {
        given:
        {
            target = loggerForCurrentClass(layerClass());

            core.allow(HANDLE);
        }
        when:
        {
            target.every(0).level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedDummy<>(layerClass()));
        }
    }

    @Test
    public void layer_allowedByPredicateButDisables_queriesForDummy() {
        given:
        {
            target = loggerForCurrentClass(layerClass());
        }
        when:
        {
            target.every(() -> true).level(Level.FINE);
        }
        then:
        {
            assertCreated(new Core.CreatedDummy<>(layerClass()));
        }
    }

    private void assertCreated(Core.Created<?>... expected) {
        assertEquals(expected.length, core.created.size());

        for (int i = 0; i < core.created.size(); i++) {
            assertEquals(expected[i], core.created.get(i));
        }
    }

    protected static class Core implements LoggingCoreApi {

        private final String name;

        private final Set<Level>       enabledLayers   = Sets.newHashSet();
        private final Set<Handle>      allowedHandlers = Sets.newHashSet();
        private final List<Created<?>> created         = Lists.newArrayList();

        protected Core(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void accept(LayerBase<? extends LayerBase<?>> layer) {
            throw new NotImplementedException("Not used in test implementation");
        }

        public void enable(Level level) {
            enabledLayers.add(level);
        }

        @Override
        public boolean isEnabled(Level level) {
            return enabledLayers.contains(level);
        }

        public void allow(Handle handle) {
            allowedHandlers.add(handle);
        }

        public void disallow(Handle handle) {
            allowedHandlers.remove(handle);
        }

        @Override
        public LoggingCoreApi.PredicateCheck isAllowed(Handle handle) {
            return new PredicateCheck(handle);
        }

        @Override
        public <T extends Layer> LoggingCoreApi.LayerClassAccessor<T> layer(Class<T> layerClass) {
            return new LayerClassAccessor<>(layerClass);
        }

        private final class PredicateCheck implements LoggingCoreApi.PredicateCheck {

            private final Handle handle;

            private PredicateCheck(Handle handle) {
                this.handle = handle;
            }

            @Override
            public boolean with(int count) {
                return allowedHandlers.contains(handle);
            }

            @Override
            public boolean with(Duration timeout) {
                return allowedHandlers.contains(handle);
            }
        }

        private final class LayerClassAccessor<T extends Layer> implements LoggingCoreApi.LayerClassAccessor<T> {

            private final Class<T> layerClass;

            private LayerClassAccessor(Class<T> layerClass) {
                this.layerClass = layerClass;
            }

            @Override
            public T create(Handle handle, Level level) {
                created.add(new CreatedLayer<>(layerClass, handle, level));
                return null;
            }

            @Override
            public T dummy() {
                created.add(new CreatedDummy<>(layerClass));
                return null;
            }
        }

        record CreatedLayer<T>(Class<T> layerClass,
                               Handle handle,
                               Level level) implements Created<T> {
        }

        record CreatedDummy<T>(Class<T> layerClass) implements Created<T> {
        }

        interface Created<T> {
            Class<T> layerClass();
        }
    }

    private record StackFrame(StackTraceElement element) implements StackWalker.StackFrame {

        @Override
        public String getClassName() {
            return element.getClassName();
        }

        @Override
        public String getMethodName() {
            return element.getMethodName();
        }

        @Override
        public Class<?> getDeclaringClass() {
            return element.getClass();
        }

        @Override
        public int getByteCodeIndex() {
            return 0;
        }

        @Override
        public String getFileName() {
            return element.getFileName();
        }

        @Override
        public int getLineNumber() {
            return element.getLineNumber();
        }

        @Override
        public boolean isNativeMethod() {
            return element.isNativeMethod();
        }

        @Override
        public StackTraceElement toStackTraceElement() {
            return element;
        }
    }
}

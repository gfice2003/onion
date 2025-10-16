package tech.grove.onion.log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.backend.LoggingBackend;
import tech.grove.onion.compiler.Compilable;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.dummy.Dummy;
import tech.grove.onion.stubs.TestLoggingContext;
import tech.grove.onion.log.context.LogContextApi;
import tech.grove.onion.stubs.TestBackendLogger;
import tech.grove.onion.stubs.TestStackHelper;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.log.AbstractLogTest.Data.*;

public abstract class AbstractLogTest<T extends Layer, L extends AbstractLog<T>> {

    interface Data {
        StackWalker.StackFrame[] STACK        = Arrays.stream(Thread.currentThread().getStackTrace()).map(StackFrame::new).toArray(StackWalker.StackFrame[]::new);
        String                   LOGGER_NAME  = STACK[0].getClassName();
        Class<?>                 LOGGER_CLASS = classForName();
        StackWalker.StackFrame   STACK_FRAME  = STACK[0];
        Handle                   HANDLE       = Handle.of(STACK_FRAME);

        static Class<?> classForName() {
            try {
                return Class.forName(LOGGER_NAME);
            } catch (Throwable ex) {
                throw new RuntimeException();
            }
        }
    }

    private static TestLoggingContext context;
    private        L                  target;
    private        T                  result;
    private        T                  anotherResult;

    @BeforeAll
    static void initializeClass() {

        LoggingBackend.initializeLoggerFactory(TestBackendLogger::new);

        AbstractLog.STACK = new TestStackHelper(STACK);
        context           = new TestLoggingContext(LOGGER_NAME);
    }

    @AfterEach
    public void cleanupTest() {

        target        = null;
        result        = null;
        anotherResult = null;

        context.reset();
    }

    protected abstract Class<T> layerClass();

    protected abstract L createTargetForCurrentClass();

    protected abstract L createTargetForClass(Class<?> logger);

    protected abstract L createTargetForName(String logger);

    private L createTarget() {
        return createTarget(context);
    }

    protected abstract L createTarget(LogContextApi context);

    @Test
    public void ctor_forCurrentClass_requestsCoreForCorrectClass() {
        given:
        {
            target = createTargetForCurrentClass();
        }
        then:
        {
            assertSame(getClass().getName(), target.context().name());
        }
    }

    @Test
    public void ctor_forLoggerClass_requestsCoreForCorrectClass() {
        given:
        {
            target = createTargetForClass(LOGGER_CLASS);
        }
        then:
        {
            assertSame(LOGGER_NAME, target.context().name());
        }
    }

    @Test
    public void ctor_forLoggerName_requestsCoreForCorrectClass() {
        given:
        {
            target = createTargetForName(LOGGER_NAME);
        }
        then:
        {
            assertSame(LOGGER_NAME, target.context().name());
        }
    }

    @Test
    public void layer_disabled_queriesForDummy() {
        given:
        {
            target = createTarget();
            context.disable(Level.FINE);
        }
        when:
        {
            result = target.level(Level.FINE);
        }
        then:
        {
            assertDummy(result);
        }
    }

    @Test
    public void layer_enabled_queriesForLayerInstance() {
        given:
        {
            target = createTarget();
        }
        when:
        {
            result = target.level(Level.FINE);
        }
        then:
        {
            assertLevel(result, Level.FINE);
        }
    }

    @Test
    public void layer_enabledAndDisabled_queriesForLayerCorrectly() {
        given:
        {
            target = createTarget();
            context.disable(Level.FINE);
        }
        when:
        {
            result        = target.level(Level.FINE);
            anotherResult = target.level(Level.CONFIG);
        }
        then:
        {
            assertDummy(result);
            assertLevel(anotherResult, Level.CONFIG);
        }
    }

    @Test
    public void layer_notAllowed_queriesForDummy() {
        given:
        {
            target = createTarget();
            context.disallow(HANDLE);
        }
        when:
        {
            result = target.every(0).level(Level.FINE);
        }
        then:
        {
            assertDummy(result);
        }
    }

    @Test
    public void layer_allowed_queriesForLayerInstance() {
        given:
        {
            target = createTarget();
        }
        when:
        {
            result = target.every(0).level(Level.FINE);
        }
        then:
        {
            assertLevel(result, Level.FINE);
        }
    }

    @Test
    public void layer_allowedAndNotAllowed_queriesForLayerCorrectly() {
        given:
        {
            target = createTarget();
        }
        when:
        {
            result = target.every(0).level(Level.CONFIG);

            context.disallow(HANDLE);

            anotherResult = target.every(0).level(Level.FINE);
        }
        then:
        {
            assertDummy(anotherResult);
            assertLevel(result, Level.CONFIG);
        }
    }


    @Test
    public void layer_notAllowedByPredicate_queriesForDummy() {
        given:
        {
            target = createTarget();
        }
        when:
        {
            result = target.every(() -> false).level(Level.FINE);
        }
        then:
        {
            assertDummy(result);
        }
    }

    @Test
    public void layer_allowedByPredicate_queriesForLayerInstance() {
        given:
        {
            target = createTarget();
        }
        when:
        {
            result = target.every(() -> true).level(Level.FINE);
        }
        then:
        {
            assertLevel(result, Level.FINE);
        }
    }

    @Test
    public void layer_allowedByPredicateAndNotAllowed_queriesForLayerCorrectly() {
        given:
        {
            target = createTarget();
        }
        when:
        {
            result        = target.every(() -> false).level(Level.FINE);
            anotherResult = target.every(() -> true).level(Level.CONFIG);
        }
        then:
        {
            assertDummy(result);
            assertLevel(anotherResult, Level.CONFIG);
        }
    }

    @Test
    public void layer_allowedButDisables_queriesForDummy() {
        given:
        {
            target = createTarget();
            context.disable(Level.FINE);
        }
        when:
        {
            result = target.every(0).level(Level.FINE);
        }
        then:
        {
            assertDummy(result);
        }
    }

    @Test
    public void layer_allowedByPredicateButDisables_queriesForDummy() {
        given:
        {
            target = createTarget();
            context.disable(Level.FINE);
        }
        when:
        {
            result = target.every(() -> true).level(Level.FINE);
        }
        then:
        {
            assertDummy(result);
        }
    }


    private void assertDummy(T result) {
        assertTrue(Proxy.isProxyClass(result.getClass()));
        assertInstanceOf(Dummy.class, Proxy.getInvocationHandler(result));
    }

    private void assertLevel(T result, Level expectedLevel) {
        assertInstanceOf(layerClass(), result);
        assertEquals(expectedLevel, result.level());
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

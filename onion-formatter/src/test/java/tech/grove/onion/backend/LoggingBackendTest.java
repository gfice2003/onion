package tech.grove.onion.backend;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.grove.onion.backend.logger.BackendLogger;
import tech.grove.onion.compiler.Compiled;

import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.backend.LoggingBackendTest.Data.*;

public class LoggingBackendTest {

    interface Data {
        String           NAME             = "name";
        String           ANOTHER_NAME     = "another name";
        Compiled<String> COMPILED         = new Compiled<>(NAME, Level.FINE, null);
        Compiled<String> ANOTHER_COMPILED = new Compiled<>(ANOTHER_NAME, Level.FINE, null);
        int              COUNT            = 54;
    }

    private LoggingBackend        target;
    private BackendLogger<String> result;
    private BackendLogger<String> anotherResult;

    @BeforeAll
    static void initializeClass() {
        LoggingBackend.initializeLoggerFactory(TestBackendLogger::new);
    }

    @BeforeEach
    public void initializeTest() {
        LoggingBackend.resetLoggers();
    }

    @AfterAll
    static void cleanupClass() {
        LoggingBackend.resetLoggerFactory();
    }

    @Test
    public void loggerFor_default_returnsValidInstance() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            result = target.loggerFor(NAME);
        }
        then:
        {
            assertInstanceOf(TestBackendLogger.class, result);
        }
    }

    @Test
    public void loggerFor_default_returnsInstanceWithCorrectName() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            result = target.loggerFor(NAME);
        }
        then:
        {
            assertEquals(NAME, result.name());
        }
    }

    @Test
    public void loggerFor_sameName_returnsSameInstance() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            result        = target.loggerFor(NAME);
            anotherResult = target.loggerFor(NAME);
        }
        then:
        {
            assertSame(result, anotherResult);
        }
    }

    @Test
    public void loggerFor_anotherName_returnsAnotherInstance() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            result        = target.loggerFor(NAME);
            anotherResult = target.loggerFor(ANOTHER_NAME);
        }
        then:
        {
            assertNotSame(result, anotherResult);
        }
    }

    @Test
    public void loggerFor_sameNameAnotherBackendInstance_returnsSameInstance() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            var anotherBackend = new LoggingBackend();

            result        = target.loggerFor(NAME);
            anotherResult = anotherBackend.loggerFor(NAME);
        }
        then:
        {
            assertSame(result, anotherResult);
        }
    }

    @Test
    public void write_default_writesDataToCorrectLogger() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            target.write(COMPILED);
        }
        then:
        {
            assertWritten(NAME, COMPILED, 1);
        }
    }

    @Test
    public void write_severalTimes_writesAllDataToCorrectLogger() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            for (int i = 0; i < COUNT; i++) {
                target.write(COMPILED);
            }
        }
        then:
        {
            assertWritten(NAME, COMPILED, COUNT);
        }
    }

    @Test
    public void write_differentLoggers_writesDataToCorrectLogger() {
        given:
        {
            target = new LoggingBackend();
        }
        when:
        {
            target.write(COMPILED);
            target.write(ANOTHER_COMPILED);
        }
        then:
        {
            assertWritten(NAME, COMPILED, 1);
            assertWritten(ANOTHER_NAME, ANOTHER_COMPILED, 1);
        }
    }

    private void assertWritten(String name, Compiled<String> expected, int count) {
        var logger = (TestBackendLogger) target.loggerFor(name);

        assertEquals(count, logger.written.size());
        assertTrue(logger.written.stream().allMatch(x -> x == expected));
    }

    private record TestBackendLogger(String name, List<Compiled<String>> written) implements BackendLogger<String> {

        public TestBackendLogger(String name) {
            this(name, Lists.newArrayList());
        }

        @Override
        public void write(Compiled<String> layer) {
            written.add(layer);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean isEnabled(Level level) {
            return true;
        }
    }
}

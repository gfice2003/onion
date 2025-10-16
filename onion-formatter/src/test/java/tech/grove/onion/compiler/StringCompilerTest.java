package tech.grove.onion.compiler;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackInfo;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.stubs.TestStackFrame;
import tech.grove.onion.utils.Field;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.grove.onion.compiler.StringCompilerTest.Data.*;

public class StringCompilerTest {

    interface Data {
        String                              LOGGER        = "logger";
        Level                               LEVEL         = Level.CONFIG;
        int                                 DEPTH         = 3;
        String                              ICON          = "(^_^)";
        Duration                            DURATION      = Duration.ofSeconds(4);
        String                              NAME          = "name";
        String                              MESSAGE       = "message";
        String                              FIELD_A_NAME  = "a";
        UUID                                FIELD_A_VALUE = UUID.fromString("d2dfeabb-dc05-4007-abf6-cad50fc9275b");
        String                              FIELD_B_NAME  = "b";
        BigDecimal                          FIELD_B_VALUE = BigDecimal.valueOf(12.3);
        Iterable<Map.Entry<String, Object>> FIELDS        = Lists.newArrayList(
                Field.of(FIELD_A_NAME, FIELD_A_VALUE),
                Field.of(FIELD_B_NAME, FIELD_B_VALUE)
        );
        ExceptionInfo                       CAUSE         = new ExceptionInfo(
                RuntimeException.class,
                "cause message",
                new StackInfo(
                        StackMode.FULL,
                        stackFor("cause", 4),
                        4),
                null);
        ExceptionInfo                       EXCEPTION     = new ExceptionInfo(
                RuntimeException.class,
                "exception message",
                new StackInfo(
                        StackMode.FAIR,
                        stackFor("exception", 2),
                        4),
                CAUSE);

        static StackWalker.StackFrame[] stackFor(String className, int size) {
            return IntStream.range(0, size)
                    .boxed()
                    .map(x ->
                                 new TestStackFrame(
                                         className,
                                         "Method_" + x,
                                         x))
                    .toArray(StackWalker.StackFrame[]::new);
        }
    }

    private final static StringCompiler target = new StringCompiler();

    private Compilable       source;
    private Compiled<String> result;

    @Test
    public void transform_loggerAndLevel_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "");
        }
    }

    @Test
    public void transform_depth_producesExpectedString() {
        given:
        {
            source = createSource(DEPTH,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "         ");
        }
    }

    @Test
    public void transform_icon_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  ICON,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "(^_^) ");
        }
    }

    @Test
    public void transform_duration_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  DURATION,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "[PT4S] ");
        }
    }

    @Test
    public void transform_name_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  NAME,
                                  null,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "name: ");
        }
    }

    @Test
    public void transform_message_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  MESSAGE,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "message ");
        }
    }

    @Test
    public void transform_fields_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  null,
                                  FIELDS,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "(a=d2dfeabb-dc05-4007-abf6-cad50fc9275b,b=12.3) ");
        }
    }

    @Test
    public void transform_stack_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  EXCEPTION.stack(),
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, """
                                           
                                           exception.Method_0 0
                                           exception.Method_1 1
                                           ...2 more""");
        }
    }

    @Test
    public void transform_exceptionNoCause_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  CAUSE);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, """
                                           
                                           Exception: class java.lang.RuntimeException:cause message
                                           cause.Method_0 0
                                           cause.Method_1 1
                                           cause.Method_2 2
                                           cause.Method_3 3""");
        }
    }

    @Test
    public void transform_exceptionWithCause_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  EXCEPTION);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, """
                    
                    Exception: class java.lang.RuntimeException:exception message
                    exception.Method_0 0
                    exception.Method_1 1
                    ...2 more
                    Caused by: class java.lang.RuntimeException:cause message
                    cause.Method_0 0
                    cause.Method_1 1
                    cause.Method_2 2
                    cause.Method_3 3""");
        }
    }


    @Test
    public void transform_depthIconNameMessage_producesExpectedString() {
        given:
        {
            source = createSource(DEPTH,
                                  ICON,
                                  null,
                                  NAME,
                                  MESSAGE,
                                  null,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "         (^_^) name: message ");
        }
    }

    @Test
    public void transform_messageFields_producesExpectedString() {
        given:
        {
            source = createSource(0,
                                  null,
                                  null,
                                  null,
                                  MESSAGE,
                                  FIELDS,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "message (a=d2dfeabb-dc05-4007-abf6-cad50fc9275b,b=12.3) ");
        }
    }

    @Test
    public void transform_depthMessageFieldsDuration_producesExpectedString() {
        given:
        {
            source = createSource(DEPTH,
                                  null,
                                  DURATION,
                                  null,
                                  MESSAGE,
                                  FIELDS,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "         message (a=d2dfeabb-dc05-4007-abf6-cad50fc9275b,b=12.3) [PT4S] ");
        }
    }

    @Test
    public void transform_depthIconDurationNameMessageFields_producesExpectedString() {
        given:
        {
            source = createSource(DEPTH,
                                  ICON,
                                  DURATION,
                                  NAME,
                                  MESSAGE,
                                  FIELDS,
                                  null,
                                  null);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, "         (^_^) name: message (a=d2dfeabb-dc05-4007-abf6-cad50fc9275b,b=12.3) [PT4S] ");
        }
    }

    @Test
    public void transform_everything_producesExpectedString() {
        given:
        {
            source = createSource(DEPTH,
                                  ICON,
                                  DURATION,
                                  NAME,
                                  MESSAGE,
                                  FIELDS,
                                  EXCEPTION.stack(),
                                  EXCEPTION);
        }
        when:
        {
            result = target.transform(source);
        }
        then:
        {
            assertCompiled(result, """
                             (^_^) name: message (a=d2dfeabb-dc05-4007-abf6-cad50fc9275b,b=12.3) [PT4S]\s
                    exception.Method_0 0
                    exception.Method_1 1
                    ...2 more
                    Exception: class java.lang.RuntimeException:exception message
                    exception.Method_0 0
                    exception.Method_1 1
                    ...2 more
                    Caused by: class java.lang.RuntimeException:cause message
                    cause.Method_0 0
                    cause.Method_1 1
                    cause.Method_2 2
                    cause.Method_3 3""");
        }
    }

    private void assertCompiled(Compiled<String> result, String expected) {
        assertEquals(Level.CONFIG, result.level());
        assertEquals(LOGGER, result.logger());
        assertEquals(expected, result.data());
    }

    private Compilable createSource(int depth,
                                    String icon,
                                    Duration duration,
                                    String name,
                                    String message,
                                    Iterable<Map.Entry<String, Object>> fields,
                                    StackInfo stack,
                                    ExceptionInfo exception) {
        return new Compilable() {
            @Override
            public String logger() {
                return LOGGER;
            }

            @Override
            public Level level() {
                return LEVEL;
            }

            @Override
            public int depth() {
                return depth;
            }

            @Override
            public String icon() {
                return icon;
            }

            @Override
            public Duration duration() {
                return duration;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String message() {
                return message;
            }

            @Override
            public Iterable<Map.Entry<String, Object>> fields() {
                return fields;
            }

            @Override
            public StackInfo stack() {
                return stack;
            }

            @Override
            public ExceptionInfo exception() {
                return exception;
            }
        };
    }
}

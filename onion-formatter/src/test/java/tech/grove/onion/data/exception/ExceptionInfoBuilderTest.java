package tech.grove.onion.data.exception;

import org.junit.jupiter.api.Test;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.tools.stack.StackHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static tech.grove.onion.data.exception.ExceptionInfoBuilderTest.Data.*;

public class ExceptionInfoBuilderTest {

    interface Data {
        String    CAUSE_MESSAGE     = "Because of me =)";
        String    EXCEPTION_MESSAGE = "Exception happened...";
        Throwable CAUSE             = new RuntimeException(CAUSE_MESSAGE);
        Throwable EXCEPTION         = new IllegalArgumentException(EXCEPTION_MESSAGE, CAUSE);
    }

    private ExceptionInfo expected;
    private ExceptionInfo result;

    @Test
    public void build_nullException_returnsNull() {
        when:
        {
            result = ExceptionInfo.of(null)
                    .noClass()
                    .noMessage()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertNull(result);
        }
    }

    @Test
    public void build_nullExceptionIncludeClass_returnsNull() {
        when:
        {
            result = ExceptionInfo.of(null)
                    .noStack()
                    .noMessage()
                    .noCause()
                    .build();
        }
        then:
        {
            assertNull(result);
        }
    }

    @Test
    public void build_nullExceptionIncludeMessage_returnsNull() {
        when:
        {
            result = ExceptionInfo.of(null)
                    .noClass()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertNull(result);
        }
    }

    @Test
    public void build_nullExceptionIncludeStack_returnsNull() {
        when:
        {
            result = ExceptionInfo.of(null)
                    .noClass()
                    .noMessage()
                    .noCause()
                    .build();
        }
        then:
        {
            assertNull(result);
        }
    }

    @Test
    public void build_includeCause_returnsNull() {
        when:
        {
            result = ExceptionInfo.of(null)
                    .noClass()
                    .noMessage()
                    .noStack()
                    .build();
        }
        then:
        {
            assertNull(result);
        }
    }

    @Test
    public void build_nullExceptionIncludeEverything_returnsNull() {
        when:
        {
            result = ExceptionInfo.of(null)
                    .build();
        }
        then:
        {
            assertNull(result);
        }
    }

    @Test
    public void build_validException_returnsInfoWithoutAnything() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    null,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(CAUSE)
                    .noClass()
                    .noMessage()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionIncludeClass_returnsInfoWithClass() {
        given:
        {
            expected = new ExceptionInfo(
                    CAUSE.getClass(),
                    null,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(CAUSE)
                    .noMessage()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionIncludeMessage_returnsInfoWithMessage() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    CAUSE_MESSAGE,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(CAUSE)
                    .noClass()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionNullStackMode_returnsInfoWithoutStack() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    null,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(CAUSE)
                    .stackMode(null)
                    .noClass()
                    .noMessage()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionIncludeStack_returnsInfoWithStack() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    null,
                    StackHelper.toInfo(CAUSE.getStackTrace()).mode(StackMode.FAIR),
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(CAUSE)
                    .noClass()
                    .noMessage()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionIncludeEverything_returnsInfoWithEverything() {
        given:
        {
            expected = new ExceptionInfo(
                    CAUSE.getClass(),
                    CAUSE_MESSAGE,
                    StackHelper.toInfo(CAUSE.getStackTrace()).mode(StackMode.FAIR),
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(CAUSE)
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionWithCause_returnsInfoWithoutAnything() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    null,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(EXCEPTION)
                    .noClass()
                    .noMessage()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionWithCauseIncludeClass_returnsInfoWithClass() {
        given:
        {
            expected = new ExceptionInfo(
                    EXCEPTION.getClass(),
                    null,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(EXCEPTION)
                    .noMessage()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionWithCauseIncludeMessage_returnsInfoWithMessage() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    EXCEPTION_MESSAGE,
                    null,
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(EXCEPTION)
                    .noClass()
                    .noStack()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionWithCauseIncludeStack_returnsInfoWithStack() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    null,
                    StackHelper.toInfo(EXCEPTION.getStackTrace()).mode(StackMode.FAIR),
                    null);
        }
        when:
        {
            result = ExceptionInfo.of(EXCEPTION)
                    .noClass()
                    .noMessage()
                    .noCause()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionWithCauseIncludeCause_returnsInfoWithCause() {
        given:
        {
            expected = new ExceptionInfo(
                    null,
                    null,
                    null,
                    new ExceptionInfo(
                            null,
                            null,
                            null,
                            null
                    ));
        }
        when:
        {
            result = ExceptionInfo.of(EXCEPTION)
                    .noClass()
                    .noMessage()
                    .noStack()
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }

    @Test
    public void build_validExceptionWithCauseIncludeEverything_returnsInfoWithEverything() {
        given:
        {
            expected = new ExceptionInfo(
                    EXCEPTION.getClass(),
                    EXCEPTION_MESSAGE,
                    StackHelper.toInfo(EXCEPTION.getStackTrace()).mode(StackMode.FAIR),
                    new ExceptionInfo(
                            CAUSE.getClass(),
                            CAUSE_MESSAGE,
                            StackHelper.toInfo(CAUSE.getStackTrace()).mode(StackMode.FAIR),
                            null
                    ));
        }
        when:
        {
            result = ExceptionInfo.of(EXCEPTION)
                    .build();
        }
        then:
        {
            assertEquals(expected, result);
        }
    }
}

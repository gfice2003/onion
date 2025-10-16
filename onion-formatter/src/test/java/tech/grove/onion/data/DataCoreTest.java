package tech.grove.onion.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.tools.stack.StackHelper;
import tech.grove.onion.utils.DataValidator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.grove.onion.data.DataCoreTest.Data.*;

public class DataCoreTest {

    interface Data {
        String                   LOGGER       = "logger";
        Level                    LEVEL        = Level.CONFIG;
        int                      DEPTH        = 3;
        String                   ICON         = "{+}";
        String                   NAME         = "Name";
        String                   PATTERN      = "Message with parameters %s %s";
        UUID                     PARAMETER_A  = UUID.randomUUID();
        LocalDateTime            PARAMETER_B  = LocalDateTime.now();
        Object[]                 PARAMETERS   = new Object[]{PARAMETER_A, PARAMETER_B};
        String                   FIELD_A      = "Object field";
        Object                   VALUE_A      = new Object();
        String                   FIELD_B      = "BigDecimal field";
        BigDecimal               VALUE_B      = BigDecimal.valueOf(234);
        Map<String, Object>      FIELDS       = Map.of(FIELD_A, VALUE_A, FIELD_B, VALUE_B);
        Duration                 DURATION     = Duration.ofMillis(324);
        Throwable                EXCEPTION    = new RuntimeException();
        StackTraceElement[]      STACK        = EXCEPTION.getStackTrace();
        StackWalker.StackFrame[] STACK_FRAMES = StackHelper.asFrames(STACK);
        StackMode                STACK_MODE   = DataCore.Default.STACK_MODE;
    }

    private DataCore      target;
    private DataValidator validator;

    @BeforeEach
    public void initializeTest() {
        validator = new DataValidator()
                .on(DataCore::logger).expecting(LOGGER)
                .on(DataCore::level).expecting(DataCore.Default.LEVEL)
                .on(DataCore::icon).expectingNull()
                .on(DataCore::name).expectingNull()
                .on(DataCore::message).expectingNull()
                .on(DataCore::rawPattern).expectingNull()
                .on(DataCore::rawParameters).expectingNull()
                .on(DataCore::fields).expecting(Set.of())
                .on(DataCore::rawFields).expectingNull()
                .on(DataCore::stack).expectingNull()
                .on(DataCore::rawStack).expectingNull()
                .on(DataCore::exception).expectingNull()
                .on(DataCore::rawException).expectingNull()
                .on(DataCore::duration).expectingNull()
                .on(DataCore::rawStackMode).expecting(DataCore.Default.STACK_MODE)
                .on(DataCore::depth).expecting(DataCore.Default.DEPTH);
    }

    @Test
    public void ctor_nullLogger_throwsArgumentNullException() {
        then:
        {
            assertThrows(ArgumentNullException.class, () -> new DataCore(null));
        }
    }

    @Test
    public void ctor_default_returnsInstanceWithDefaultValues() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    public void withLevel_null_leavesLevelDefault() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withLevel(null);
        }
        then:
        {
            validator.validate(target);
        }
    }

    @Test
    public void withLevel_anotherValue_changesLevel() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withLevel(LEVEL);
        }
        then:
        {
            validator
                    .on(DataCore::level).expecting(LEVEL)
                    .validate(target);
        }
    }

    @Test
    public void withDepth_negative_leavesDepthDefault() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withDepth(-1);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withDepth_valid_changesDepth() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withDepth(DEPTH);
        }
        then:
        {
            validator
                    .on(DataCore::depth).expecting(DEPTH)
                    .validate(target);
        }
    }

    @Test
    public void withIcon_null_leavesIconNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withIcon(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withIcon_valid_changesIcon() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withIcon(ICON);
        }
        then:
        {
            validator
                    .on(DataCore::icon).expecting(ICON)
                    .validate(target);
        }
    }

    @Test
    public void withName_null_leavesNameNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withName(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withName_valid_changesName() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withName(NAME);
        }
        then:
        {
            validator
                    .on(DataCore::name).expecting(NAME)
                    .validate(target);
        }
    }

    @Test
    public void withPattern_null_leavesPatternsAndMessageNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withPattern(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withPattern_valid_changesPatternAndMessage() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withPattern(PATTERN);
        }
        then:
        {
            validator
                    .on(DataCore::rawPattern).expecting(PATTERN)
                    .on(DataCore::message).expecting(PATTERN)
                    .validate(target);
        }
    }

    @Test
    public void withParameters_null_leavesParametersAndMessageNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withParameters(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withPattern_valid_changesParametersAndMessage() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withParameters(PARAMETERS);
        }
        then:
        {
            validator
                    .on(DataCore::rawParameters).expecting(PARAMETERS)
                    .on(DataCore::message).expecting(PARAMETER_A + "," + PARAMETER_B)
                    .validate(target);
        }
    }

    @Test
    public void withPatternAndParameters_valid_changesPatternParametersAndMessage() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withPattern(PATTERN);
            target.withParameters(PARAMETERS);
        }
        then:
        {
            validator
                    .on(DataCore::rawPattern).expecting(PATTERN)
                    .on(DataCore::rawParameters).expecting(PARAMETERS)
                    .on(DataCore::message).expecting(PATTERN.formatted(PARAMETERS))
                    .validate(target);
        }
    }

    @Test
    public void withField_nullName_leavesFieldsNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withField(null, VALUE_A);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withField_validData_registersField() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withField(FIELD_A, VALUE_A);
        }
        then:
        {
            validator
                    .on(DataCore::rawFields).expecting(Map.of(FIELD_A, VALUE_A))
                    .on(DataCore::fields).expecting(Map.of(FIELD_A, VALUE_A).entrySet())
                    .validate(target);
        }
    }

    @Test
    public void withField_severalFields_registersAllField() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withField(FIELD_A, VALUE_A);
            target.withField(FIELD_B, VALUE_B);
        }
        then:
        {
            validator
                    .on(DataCore::fields).expecting(FIELDS.entrySet())
                    .on(DataCore::rawFields).expecting(FIELDS)
                    .validate(target);
        }
    }

    @Test
    public void withFields_null_leavesFieldsNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withFields(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withFields_valid_setsFields() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withFields(FIELDS);
        }
        then:
        {
            validator
                    .on(DataCore::fields).expecting(FIELDS.entrySet())
                    .on(DataCore::rawFields).expecting(FIELDS)
                    .validate(target);
        }
    }

    @Test
    public void withDuration_null_leavesDurationNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withDuration(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withDuration_valid_setsDuration() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withDuration(DURATION);
        }
        then:
        {
            validator
                    .on(DataCore::duration).expecting(DURATION)
                    .validate(target);
        }
    }

    @Test
    public void withStack_null_leavesStackNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withStack(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withStack_valid_setsStack() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withStack(STACK_FRAMES);
        }
        then:
        {
            validator
                    .on(DataCore::stack).expecting(StackHelper.toInfo(STACK_FRAMES).mode(DataCore.Default.STACK_MODE))
                    .on(DataCore::rawStack).expecting(STACK_FRAMES)
                    .validate(target);
        }
    }

    @Test
    public void withException_null_leavesExceptionNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withException(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withException_valid_setsException() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withStack(STACK_FRAMES);
        }
        then:
        {
            validator
                    .on(DataCore::stack).expecting(StackHelper.toInfo(STACK_FRAMES).mode(DataCore.Default.STACK_MODE))
                    .on(DataCore::rawStack).expecting(STACK_FRAMES)
                    .validate(target);
        }
    }

    @Test
    public void withStackMode_null_leavesStackModeNull() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withStackMode(null);
        }
        then:
        {
            validator
                    .validate(target);
        }
    }

    @Test
    public void withStackMode_valid_setsStackMode() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withStackMode(STACK_MODE);
        }
        then:
        {
            validator
                    .on(DataCore::rawStackMode).expecting(STACK_MODE)
                    .validate(target);
        }
    }

    @Test
    public void withStackModeAndException_valid_setsStackModeAndException() {
        given:
        {
            target = new DataCore(LOGGER);
        }
        when:
        {
            target.withStackMode(STACK_MODE);
            target.withException(EXCEPTION);
        }
        then:
        {
            validator
                    .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).stackMode(STACK_MODE).build())
                    .on(DataCore::rawException).expecting(EXCEPTION)
                    .on(DataCore::rawStackMode).expecting(STACK_MODE)
                    .validate(target);
        }
    }
}

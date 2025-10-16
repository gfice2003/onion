package tech.grove.onion.data.builders.base;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.BuilderConsumer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.tools.stack.StackHelper;
import tech.grove.onion.utils.Field;
import tech.grove.onion.utils.DataValidator;
import tech.grove.onion.stubs.TestHandle;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.utils.StackUtils.stackOfSize;
import static tech.grove.onion.data.builders.base.BaseBuilderTest.Data.*;

public abstract class BaseBuilderTest<T extends BaseBuilder<T>> {

    protected interface Data {
        String                   LOGGER     = "TestLogger";
        Level                    LEVEL      = Level.INFO;
        StackWalker.StackFrame[] STACK      = stackOfSize(StackHelper.Default.LIMIT + 2);
        Instant                  TIMESTAMP  = Instant.now();
        AtomicReference<Instant> NOW        = new AtomicReference<>(TIMESTAMP);
        String                   PATTERN    = "pattern";
        Object[]                 PARAMETERS = new Object[]{new Object(), new Object()};
        Field                    FIELD_1    = Field.of("Field #1", 1);
        Field                    FIELD_NULL = Field.of("Field NULL", null);
        Field                    FIELD_2A   = Field.of("Field #2", "A");
        Field                    FIELD_2B   = Field.of("Field #2", "B");
        Handle                   HANDLE     = new TestHandle("class", "method", 4);
    }

    protected T                   target;
    protected TestBuilderConsumer consumer;
    protected DataValidator       validator;

    @BeforeEach
    public void initializeTest() {

        consumer  = new TestBuilderConsumer();
        validator = createValidator();

        NOW.set(TIMESTAMP);
    }

    @Nested
    class BaseLayerStructureTest {

        @Test
        public void ctor_noConsumer_throwsArgumentNullException() {
            then:
            {
                assertThrows(ArgumentNullException.class, BaseBuilderTest.this::createTargetNoConsumer);
            }
        }

        @Test
        public void ctor_noData_throwsArgumentNullException() {
            then:
            {
                assertThrows(ArgumentNullException.class, BaseBuilderTest.this::createTargetNoData);
            }
        }

        @Test
        public void ctor_default_returnsCorrectInstance() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                validator
                        .validate(target.data());
            }
        }

        @Test
        public void commit_default_callsCoreAcceptWithLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.commit();
            }
            then:
            {
                assertEquals(1, consumer.entries.size());
                assertSame(target, consumer.entries.getFirst());
            }
        }
    }

    protected T createTarget() {
        return createTarget(consumer, createData());
    }

    protected void createTargetNoConsumer() {
        createTarget(null, createData());
    }

    protected void createTargetNoData() {
        createTarget(consumer, null);
    }

    private DataCore createData() {
        return new DataCore(LOGGER) {{
            withLevel(LEVEL);
        }};
    }

    protected abstract T createTarget(Consumer<? super BaseBuilder<?>> consumer, DataCore data);

    protected DataValidator createValidator() {
        return new DataValidator()
                .on(DataCore::level).expecting(LEVEL)
                .on(DataCore::logger).expecting(LOGGER);
    }

    protected static class TestBuilderConsumer implements BuilderConsumer {

        private final List<BaseBuilder<?>> entries = Lists.newArrayList();

        @Override
        public void accept(BaseBuilder<?> entry) {
            entries.add(entry);
        }

        public List<BaseBuilder<?>> entries() {
            return entries;
        }
    }
}

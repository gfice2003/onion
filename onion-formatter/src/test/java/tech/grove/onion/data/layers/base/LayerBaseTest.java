package tech.grove.onion.data.layers.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.api.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.fields.Field;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.tools.StackHelper;
import tech.grove.onion.utils.LayerValidator;
import tech.grove.onion.utils.TestHandle;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.utils.StackUtils.stackOfSize;
import static tech.grove.onion.data.layers.base.LayerBaseTest.Data.*;

public abstract class LayerBaseTest<T extends LayerBase<T>> {

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

    protected T                 target;
    protected Core              core;
    protected LayerValidator<T> validator;

    @BeforeEach
    public void initializeTest() {

        core      = createCore();
        validator = createValidator();

        NOW.set(TIMESTAMP);
    }

    @Nested
    class BaseLayerStructureTest {

        @Test
        public void ctor_noCore_throwsArgumentNullException() {
            then:
            {
                assertThrows(ArgumentNullException.class, LayerBaseTest.this::createTargetNoCore);
            }
        }

        @Test
        public void ctor_noContext_throwsArgumentNullException() {
            then:
            {
                assertThrows(ArgumentNullException.class, LayerBaseTest.this::createTargetNoContext);
            }
        }

        @Test
        public void ctor_noLevel_returnsInstanceInstantiatedWithDefaultLevel() {
            given:
            {
                target = createTargetNoLevel();
            }
            then:
            {
                assertEquals(LayerBase.Default.LEVEL, target.level());
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
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void promoteTo_nullLevel_leavesLevelUnchanged() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.promoteTo(null);
            }
            then:
            {
                assertEquals(Data.LEVEL, target.level());
            }
        }

        @Test
        public void promoteTo_lowerLevel_changesLevel() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.promoteTo(Level.FINER);
            }
            then:
            {
                assertEquals(Level.FINER, target.level());
            }
        }

        @Test
        public void promoteTo_higherLevel_changesLevel() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.promoteTo(Level.SEVERE);
            }
            then:
            {
                assertEquals(Level.SEVERE, target.level());
            }
        }

        @Test
        public void formatWith_nullPatternAndParameters_leavesBothNulls() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.format(null).with(null);
            }
            then:
            {
                assertNull(target.pattern());
                assertNull(target.parameters());
            }
        }

        @Test
        public void formatWith_nullPatternWithParameters_initializesParameters() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.format(null).with(Data.PARAMETERS);
            }
            then:
            {
                assertNull(target.pattern());
                assertEquals(Data.PARAMETERS, target.parameters());
            }
        }

        @Test
        public void formatWith_validPatternNullParameters_initializesPattern() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.format(Data.PATTERN).with(null);
            }
            then:
            {
                assertEquals(Data.PATTERN, target.pattern());
                assertNull(target.parameters());
            }
        }

        @Test
        public void formatWith_validPatternWithParameters_initializesPattern() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.format(Data.PATTERN).with(Data.PARAMETERS);
            }
            then:
            {
                assertEquals(Data.PATTERN, target.pattern());
                assertEquals(Data.PARAMETERS, target.parameters());
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
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());
            }
        }
    }

    @Nested
    class FieldsFunctionality {

        @Test
        public void ctor_default_leavesFieldsNull() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                assertNull(target.fields());
            }
        }

        @Test
        public void registerField_nullName_throwsArgumentNullException() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                assertThrows(ArgumentNullException.class, () -> target.registerField(null, FIELD_1.value()));
            }
        }

        @Test
        public void registerField_nullValue_addsFieldWithNullValue() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.registerField(FIELD_NULL.name(), FIELD_NULL.value());
            }
            then:
            {
                verify(target.fields(), FIELD_NULL);
            }
        }

        @Test
        public void registerField_default_addsExpectedField() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.registerField(FIELD_1.name(), FIELD_1.value());
            }
            then:
            {
                verify(target.fields(), FIELD_1);
            }
        }

        @Test
        public void registerField_twoFields_addsBothFields() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.registerField(FIELD_1.name(), FIELD_1.value());
                target.registerField(FIELD_2A.name(), FIELD_2A.value());
            }
            then:
            {
                verify(target.fields(), FIELD_1, FIELD_2A);
            }
        }

        @Test
        public void registerField_severalFieldsWithSameName_addsAllFields() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.registerField(FIELD_1.name(), FIELD_1.value());
                target.registerField(FIELD_2A.name(), FIELD_2A.value());
                target.registerField(FIELD_2B.name(), FIELD_2B.value());
            }
            then:
            {
                verify(target.fields(), FIELD_1, FIELD_2A, FIELD_2B);
            }
        }
    }

    private void verify(Iterable<Field> fields, Field... expected) {
        var set = Sets.newHashSet(expected);

        for (var field : fields) {
            if (!set.remove(field)) {
                fail("Unexpected field %s".formatted(field));
            }
        }

        for (var field : set) {
            fail("Missing field %s".formatted(field));
        }
    }

    protected T createTarget() {
        return createTarget(core, HANDLE, Data.LEVEL, NOW::get);
    }

    protected void createTargetNoCore() {
        createTarget(null, HANDLE, Data.LEVEL, NOW::get);
    }

    protected void createTargetNoContext() {
        createTarget(core, null, Data.LEVEL, NOW::get);
    }

    protected T createTargetNoLevel() {
        return createTarget(core, HANDLE, null, NOW::get);
    }

    protected abstract T createTarget(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now);

    protected abstract LayerBase.Type layerType();

    protected Core createCore() {
        return new Core(Data.LOGGER);
    }

    protected LayerValidator<T> createValidator() {
        return new LayerValidator<T>()
                .on(LayerBase::core).expecting(core)
                .on(LayerBase::handle).expecting(HANDLE)
                .on(LayerBase::type).expecting(layerType())
                .on(LayerBase::level).expecting(Data.LEVEL)
                .on(LayerBase::timestamp).expecting(Data.TIMESTAMP)
                .on(LayerBase::pattern).expecting(Data.PATTERN)
                .on(LayerBase::parameters).expecting(Data.PARAMETERS);
    }

    protected static class Core implements LoggingCoreApi {

        private final String                                  name;
        private final List<LayerBase<? extends LayerBase<?>>> layers = Lists.newArrayList();

        protected Core(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void accept(LayerBase<? extends LayerBase<?>> layer) {
            layers.add(layer);
        }

        public List<LayerBase<? extends LayerBase<?>>> layers() {
            return layers;
        }

        @Override
        public boolean isEnabled(Level level) {
            throw new NotImplementedException("Not used in test implementation");
        }

        @Override
        public PredicateCheck isAllowed(Handle handle) {
            throw new NotImplementedException("Not used in test implementation");
        }

        @Override
        public <T extends Layer> LayerClassAccessor<T> layer(Class<T> layerClass) {
            throw new NotImplementedException("Not used in test implementation");
        }
    }
}

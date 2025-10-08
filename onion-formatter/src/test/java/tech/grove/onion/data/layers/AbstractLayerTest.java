package tech.grove.onion.data.layers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.layers.base.LayerBaseTest;
import tech.grove.onion.data.layers.sandwich.SandwichIn;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.tools.StackHelper;
import tech.grove.onion.utils.LayerValidator;
import tech.grove.onion.utils.TestStackHelper;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.data.layers.AbstractLayerTest.Data.*;

public abstract class AbstractLayerTest<T extends AbstractLayer<T>> extends LayerBaseTest<T> {

    interface Data extends LayerBaseTest.Data {
        Exception EXCEPTION  = new RuntimeException("This is exception");
        String    LAYER_NAME = "Sandwich layer";
    }

    @BeforeAll
    static void initializeClass() {
        AbstractLayer.STACK_GETTER = new TestStackHelper(STACK);
    }

    @Nested
    class StackFunctionality {
        @Test
        public void putStack_null_doesNotInitializeStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.putStack(null);
            }
            then:
            {
                assertNull(target.stack());
            }
        }

        @Test
        public void putStack_NONE_initializesStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.putStack(StackMode.NONE);
            }
            then:
            {
                assertEquals(StackMode.NONE, target.stack().mode());
                assertEquals(STACK.length, target.stack().size());
                assertEquals(0, target.stack().elements().length);
            }
        }

        @Test
        public void putStack_FAIR_initializesTrimmedStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.putStack(StackMode.FAIR);
            }
            then:
            {
                var trimmedStack = StackHelper.trim(STACK)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);

                assertEquals(StackMode.FAIR, target.stack().mode());
                assertEquals(Data.STACK.length, target.stack().size());
                assertArrayEquals(trimmedStack, target.stack().elements());
            }
        }

        @Test
        public void putStack_FULL_initializesTrimmedStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.putStack(StackMode.FULL);
            }
            then:
            {
                assertEquals(StackMode.FULL, target.stack().mode());
                assertEquals(Data.STACK.length, target.stack().size());
                assertArrayEquals(STACK, target.stack().elements());
            }
        }
    }

    @Nested
    class ExceptionFunctionality {
        @Test
        public void exception_null_doesNotInitializeException() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(null);
            }
            then:
            {
                assertNull(target.exception());
            }
        }

        @Test
        public void exception_validException_initializesExceptionWithDefaultFormat() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION);
            }
            then:
            {
                var stackInfo = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.FAIR);

                assertEquals(RuntimeException.class, target.exception().type());
                assertEquals(EXCEPTION.getMessage(), target.exception().message());
                assertEquals(stackInfo, target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }

        @Test
        public void exception_validExceptionNoMessage_initializesExceptionWithoutMessage() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION).noMessage();
            }
            then:
            {
                var stackInfo = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.FAIR);

                assertEquals(RuntimeException.class, target.exception().type());
                assertNull(target.exception().message());
                assertEquals(stackInfo, target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }

        @Test
        public void exception_validExceptionNoClass_initializesExceptionWithoutType() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION).noClass();
            }
            then:
            {
                var stackInfo = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.FAIR);

                assertNull(target.exception().type());
                assertEquals(EXCEPTION.getMessage(), target.exception().message());
                assertEquals(stackInfo, target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }

        @Test
        public void exception_validExceptionNoStack_initializesExceptionWithoutStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION).noStack();
            }
            then:
            {
                assertEquals(RuntimeException.class, target.exception().type());
                assertEquals(EXCEPTION.getMessage(), target.exception().message());
                assertNull(target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }

        @Test
        public void exception_validExceptionStackNONE_initializesExceptionWithEmptyStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION).stackMode(StackMode.NONE);
            }
            then:
            {
                var stackInfo = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.NONE);

                assertEquals(RuntimeException.class, target.exception().type());
                assertEquals(EXCEPTION.getMessage(), target.exception().message());
                assertEquals(stackInfo, target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }

        @Test
        public void exception_validExceptionStackFAIR_initializesExceptionWithEmptyStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION).stackMode(StackMode.FAIR);
            }
            then:
            {
                var stackInfo = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.FAIR);

                assertEquals(RuntimeException.class, target.exception().type());
                assertEquals(EXCEPTION.getMessage(), target.exception().message());
                assertEquals(stackInfo, target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }

        @Test
        public void exception_validExceptionStackFULL_initializesExceptionWithEmptyStack() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION).stackMode(StackMode.FULL);
            }
            then:
            {
                var stackInfo = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.FULL);

                assertEquals(RuntimeException.class, target.exception().type());
                assertEquals(EXCEPTION.getMessage(), target.exception().message());
                assertEquals(stackInfo, target.exception().stack());
                assertNull(target.exception().causedBy());
            }
        }
    }

    @Nested
    class SandwichLayerFunctionality {

        private LayerValidator<SandwichIn> sandwichValidator;

        @BeforeEach
        public void initializeTest() {
            sandwichValidator = new LayerValidator<SandwichIn>()
                    .on(LayerBase::core).expecting(core)
                    .on(LayerBase::handle).expecting(HANDLE)

                    .on(LayerBase::type).expecting(LayerBase.Type.IN)
                    .on(LayerBase::level).expecting(LEVEL)
                    .on(LayerBase::timestamp).expecting(TIMESTAMP)
                    .on(LayerBase::pattern).expectingNull()
                    .on(LayerBase::parameters).expectingNull();
        }

        @Test
        public void layerAdd_default_addsNewSandwichInInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.layer(LAYER_NAME).add();
            }
            then:
            {
                assertEquals(1, core.layers().size());

                if (core.layers().getFirst() instanceof SandwichIn sandwich) {
                    sandwichValidator.validate(sandwich);
                } else {
                    fail("Unexpected layer %s".formatted(core.layers().getFirst()));
                }
            }
        }

        @Test
        public void layerAdd_validPatternNullParameters_addsNewSandwichInInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.layer(LAYER_NAME).add(PATTERN);
            }
            then:
            {
                assertEquals(1, core.layers().size());

                if (core.layers().getFirst() instanceof SandwichIn sandwich) {
                    sandwichValidator
                            .on(LayerBase::pattern).expecting(PATTERN)
                            .validate(sandwich);
                } else {
                    fail("Unexpected layer %s".formatted(core.layers().getFirst()));
                }
            }
        }

        @Test
        public void layerAdd_nullPatternWithParameters_addsNewSandwichInInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.layer(LAYER_NAME).add(null, PARAMETERS);
            }
            then:
            {
                assertEquals(1, core.layers().size());

                if (core.layers().getFirst() instanceof SandwichIn sandwich) {
                    sandwichValidator
                            .on(LayerBase::parameters).expecting(PARAMETERS)
                            .validate(sandwich);
                } else {
                    fail("Unexpected layer %s".formatted(core.layers().getFirst()));
                }
            }
        }

        @Test
        public void layerAdd_validPatternWithParameters_addsNewSandwichInInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.layer(LAYER_NAME).add(PATTERN, PARAMETERS);
            }
            then:
            {
                assertEquals(1, core.layers().size());

                if (core.layers().getFirst() instanceof SandwichIn sandwich) {
                    sandwichValidator
                            .on(LayerBase::pattern).expecting(PATTERN)
                            .on(LayerBase::parameters).expecting(PARAMETERS)
                            .validate(sandwich);
                } else {
                    fail("Unexpected layer %s".formatted(core.layers().getFirst()));
                }
            }
        }
    }

    @Nested
    class FinalActionFunctionality {

        @Test
        public void add_default_addsNewLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.add();
            }
            then:
            {
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());

                validator
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void add_validPatternNullParameters_addsNewLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.add(PATTERN);
            }
            then:
            {
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());

                validator
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void add_nullPatternWithParameters_addsNewLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.add(null, PARAMETERS);
            }
            then:
            {
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());

                validator
                        .on(LayerBase::pattern).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void add_validPatternWithParameters_addsNewLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.add(PATTERN, PARAMETERS);
            }
            then:
            {
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());

                validator.validate(target);
            }
        }
    }

    @Nested
    class ComplexApiUsageScenarios {

        @Test
        public void stack_validPatternWithParameters_addsNewLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.putStack(StackMode.FAIR)
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());

                var trimmedStack = StackHelper.toInfo(STACK)
                        .mode(StackMode.FAIR);

                validator
                        .on(AbstractLayer::stack).expecting(trimmedStack)
                        .validate(target);
            }
        }

        @Test
        public void exception_validPatternWithParameters_addsNewLayerInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.exception(EXCEPTION)
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                assertEquals(1, core.layers().size());
                assertSame(target, core.layers().getFirst());

                var trimmedStack = StackHelper.toInfo(EXCEPTION.getStackTrace())
                        .mode(StackMode.FAIR);

                validator
                        .on(x -> x.exception().type()).expecting(EXCEPTION.getClass())
                        .on(x -> x.exception().message()).expecting(EXCEPTION.getMessage())
                        .on(x -> x.exception().stack()).expecting(trimmedStack)
                        .on(x -> x.exception().causedBy()).expectingNull()
                        .validate(target);
            }
        }
    }
}

package tech.grove.onion.data.builders.layer;

import org.junit.jupiter.api.*;
import tech.grove.onion.api.shell.Adder;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.base.BaseBuilderTest;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.tools.stack.StackHelper;
import tech.grove.onion.stubs.TestStackHelper;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.data.builders.layer.LayerBuilderTest.Data.*;

public abstract class LayerBuilderTest<T extends LayerBuilder<T>> extends BaseBuilderTest<T> {

    interface Data extends BaseBuilderTest.Data {
        Exception EXCEPTION  = new RuntimeException("This is exception");
        String    LAYER_NAME = "Sandwich layer";
    }

    @BeforeAll
    static void initializeClass() {
        LayerBuilder.STACK_GETTER = new TestStackHelper(STACK);
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
               validator.validate(target.data());
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
                validator
                        .on(DataCore::stack).expecting(StackHelper.toInfo(STACK).mode(StackMode.NONE))
                        .validate(target.data());
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
                validator
                        .on(DataCore::stack).expecting(StackHelper.toInfo(STACK).mode(StackMode.FAIR))
                        .validate(target.data());
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
                validator
                        .on(DataCore::stack).expecting(StackHelper.toInfo(STACK).mode(StackMode.FULL))
                        .validate(target.data());
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
                Assertions.assertNull(target.data().exception());
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
                validator
                        .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).build())
                        .validate(target.data());
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
                validator
                        .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).stackMode(StackMode.NONE).build())
                        .validate(target.data());
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
                validator
                        .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).stackMode(StackMode.NONE).build())
                        .validate(target.data());
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
                validator
                        .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).stackMode(StackMode.FAIR).build())
                        .validate(target.data());
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
                validator
                        .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).stackMode(StackMode.FULL).build())
                        .validate(target.data());
            }
        }
    }

    @Nested
    class ShellBuilderFunctionality {

        private Adder result;

        @Test
        public void shell_nullName_createsShellBuilderWithDefaultName() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.shell(null);
            }
            then:
            {
                if (result instanceof ShellBuilder shell) {
                    validator
                            .on(DataCore::name).expecting(LayerBuilder.Token.NO_NAME)
                            .validate(shell.data());
                } else {
                    Assertions.fail("Unexpected result %s".formatted(consumer.entries().getFirst()));
                }
            }
        }

        @Test
        public void shell_validName_createsShellBuilderWithDefaultName() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.shell(LAYER_NAME);
            }
            then:
            {
                if (result instanceof ShellBuilder shell) {
                    validator
                            .on(DataCore::name).expecting(LAYER_NAME)
                            .validate(shell.data());
                } else {
                    Assertions.fail("Unexpected result %s".formatted(consumer.entries().getFirst()));
                }
            }
        }

        @Test
        public void methodShell_default_createsValidShellBuilder() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.methodShell();
            }
            then:
            {
                if (result instanceof ShellBuilder shell) {
                    validator
                            .on(DataCore::name).expecting("methodShell_default_createsValidShellBuilder")
                            .validate(shell.data());
                } else {
                    Assertions.fail("Unexpected result %s".formatted(consumer.entries().getFirst()));
                }
            }
        }
    }

    @Nested
    class FinalActionFunctionality {

        @Test
        public void add_default_addsCorrectBuilderInstance() {
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
                assertEquals(1, consumer.entries().size());
                Assertions.assertSame(target, consumer.entries().getFirst());

                validator
                        .validate(target.data());
            }
        }

        @Test
        public void add_validPatternNullParameters_addsCorrectBuilderInstance() {
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
                assertEquals(1, consumer.entries().size());
                Assertions.assertSame(target, consumer.entries().getFirst());

                validator
                        .on(DataCore::message).expecting(PATTERN)
                        .validate(target.data());
            }
        }

        @Test
        public void add_nullPatternWithParameters_addsCorrectBuilderInstance() {
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
                assertEquals(1, consumer.entries().size());
                Assertions.assertSame(target, consumer.entries().getFirst());

                validator
                        .on(DataCore::message).expecting(Arrays.stream(PARAMETERS).map(Object::toString).collect(Collectors.joining(",")))
                        .validate(target.data());
            }
        }

        @Test
        public void add_validPatternWithParameters_addsCorrectBuilderInstance() {
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
                assertEquals(1, consumer.entries().size());
                Assertions.assertSame(target, consumer.entries().getFirst());

                validator
                        .on(DataCore::message).expecting(PATTERN.formatted(PARAMETERS))
                        .validate(target.data());
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
                assertEquals(1, consumer.entries().size());
                Assertions.assertSame(target, consumer.entries().getFirst());

                validator
                        .on(DataCore::stack).expecting(StackHelper.toInfo(STACK).mode(StackMode.FAIR))
                        .validate(target.data());
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
                assertEquals(1, consumer.entries().size());
                Assertions.assertSame(target, consumer.entries().getFirst());

                validator
                        .on(DataCore::exception).expecting(ExceptionInfo.of(EXCEPTION).stackMode(StackMode.FAIR).build())
                        .validate(target.data());
            }
        }
    }
}

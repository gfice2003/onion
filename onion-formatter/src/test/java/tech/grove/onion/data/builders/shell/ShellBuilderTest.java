package tech.grove.onion.data.builders.shell;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.api.shell.Report;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.base.BaseBuilderTest;
import tech.grove.onion.utils.DataValidator;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.tools.Cast.cast;
import static tech.grove.onion.data.builders.shell.ShellBuilderTest.Data.*;

public class ShellBuilderTest extends BaseBuilderTest<ShellBuilder> {

    interface Data extends BaseBuilderTest.Data {
    }

    @Override
    protected ShellBuilder createTarget(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        return new ShellBuilder(consumer, data);
    }

    @Nested
    class ShellBuilderFunctionality {

        private Report result;

        @Test
        public void add_default_passesInstanceToConsumer() {
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

                validator
                        .validate(consumer.entries().getFirst().data());
            }
        }

        @Test
        public void add_default_returnsValidShellReportBuilderInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.add();
            }
            then:
            {
                if (result instanceof ShellReportBuilder report) {
                    resultValidator()
                            .validate(report.data());
                } else {
                    fail("Result is of unexpected type %s".formatted(result.getClass()));
                }
            }
        }

        @Test
        public void add_patternAndParameter_setsMessageForInstance() {
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

                validator
                        .on(DataCore::message).expecting(PATTERN.formatted(PARAMETERS))
                        .validate(consumer.entries().getFirst().data());
            }
        }
    }

    private DataValidator resultValidator() {
        try {
            return cast(validator.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

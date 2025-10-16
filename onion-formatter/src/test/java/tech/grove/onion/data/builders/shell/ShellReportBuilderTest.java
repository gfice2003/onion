package tech.grove.onion.data.builders.shell;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.base.BaseBuilderTest;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.data.builders.shell.ShellReportBuilderTest.Data.*;

public class ShellReportBuilderTest extends BaseBuilderTest<ShellReportBuilder> {

    interface Data extends BaseBuilderTest.Data {
        Object              RESULT          = new Object();
        Map<String, Object> RESULT_MAP      = Map.of(ShellReportBuilder.Token.RESULT_FIELD, RESULT);
        Iterable<?>         EXPECTED_FIELDS = RESULT_MAP.entrySet();
    }

    @Override
    protected ShellReportBuilder createTarget(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        return new ShellReportBuilder(consumer, data);
    }

    @Nested
    class ShellReportBuilderFunctionality {

        private Object result;

        @Test
        public void ctor_default_doesNotInitializePatternAndParameters() {
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
        public void ctor_default_setsIsSkippedFalse() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                assertFalse(target.isSkipped());
            }
        }

        @Test
        public void skip_default_setsIsSkippedFlag() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.skip();
            }
            then:
            {
                assertTrue(target.isSkipped());
            }
        }

        @Test
        public void report_patternAndParameters_initializesPatternAndParametersAndLeavesLevelUnchanged() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.report(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(DataCore::message).expecting(PATTERN.formatted(PARAMETERS))
                        .validate(target.data());
            }
        }

        @Test
        public void report_object_initializesParametersAndLeavesLevelUnchanged() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.report(RESULT);
            }
            then:
            {
                validator
                        .on(DataCore::fields).expecting(EXPECTED_FIELDS)
                        .validate(target.data());
            }
        }

        @Test
        public void report_object_returnsPassedObjectAsAResult() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.report(RESULT);
            }
            then:
            {
                assertSame(RESULT, result);
            }
        }

        @ParameterizedTest
        @MethodSource("levelMethodPatternAndParameters")
        public void report_levelMethodPatternAndParameters_modifiesLayerCorrectly(ReportLevelAction action, Level expectedLevel) {
            given:
            {
                target = createTarget();
            }
            when:
            {
                action.apply(target, PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(DataCore::level).expecting(expectedLevel)
                        .on(DataCore::message).expecting(PATTERN.formatted(PARAMETERS))
                        .validate(target.data());
            }
        }

        @ParameterizedTest
        @FieldSource("levels")
        public void report_levelPatternAndParameters_modifiesLayerCorrectly(Level expectedLevel) {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.report(expectedLevel, PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(DataCore::level).expecting(expectedLevel)
                        .on(DataCore::message).expecting(PATTERN.formatted(PARAMETERS))
                        .validate(target.data());
            }
        }

        @ParameterizedTest
        @MethodSource("levelMethodAndResult")
        public void report_levelMethodAndResult_modifiesLayerCorrectly(ReportResultAction action, Level expectedLevel) {
            given:
            {
                target = createTarget();
            }
            when:
            {
                action.apply(target, RESULT);
            }
            then:
            {
                validator
                        .on(DataCore::level).expecting(expectedLevel)
                        .on(DataCore::fields).expecting(EXPECTED_FIELDS)
                        .validate(target.data());
            }
        }

        @ParameterizedTest
        @MethodSource("levelMethodAndResult")
        public void report_leveMethodAndResult_returnsCorrectResult(ReportResultAction action, Level ignored) {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = action.apply(target, RESULT);
            }
            then:
            {
                assertSame(RESULT, result);
            }
        }

        @ParameterizedTest
        @FieldSource("levels")
        public void report_levelAndResult_modifiesLayerCorrectly(Level expectedLevel) {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.report(expectedLevel, RESULT);
            }
            then:
            {
                validator
                        .on(DataCore::level).expecting(expectedLevel)
                        .on(DataCore::fields).expecting(EXPECTED_FIELDS)
                        .validate(target.data());
            }
        }

        @ParameterizedTest
        @FieldSource("levels")
        public void report_leveAndResult_returnsCorrectResult(Level expectedLevel) {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = target.report(expectedLevel, RESULT);
            }
            then:
            {
                assertSame(RESULT, result);
            }
        }

        private static final List<Level> levels = List.of(Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE);

        private static Stream<Arguments> levelMethodPatternAndParameters() {
            return Stream.of(
                    argumentsOf((u, p, r) -> u.reportFinest(p, r), Level.FINEST),
                    argumentsOf((u, p, r) -> u.reportFiner(p, r), Level.FINER),
                    argumentsOf((u, p, r) -> u.reportFine(p, r), Level.FINE),
                    argumentsOf((u, p, r) -> u.reportConfig(p, r), Level.CONFIG),
                    argumentsOf((u, p, r) -> u.reportInfo(p, r), Level.INFO),
                    argumentsOf((u, p, r) -> u.reportWarning(p, r), Level.WARNING),
                    argumentsOf((u, p, r) -> u.reportSevere(p, r), Level.SEVERE)
            );
        }

        private static Arguments argumentsOf(ReportLevelAction action, Level expectedLevel) {
            return Arguments.of(action, expectedLevel);
        }

        private static Stream<Arguments> levelMethodAndResult() {
            return Stream.of(
                    argumentsOf((o, r) -> o.reportFinest(r), Level.FINEST),
                    argumentsOf((o, r) -> o.reportFiner(r), Level.FINER),
                    argumentsOf((o, r) -> o.reportFine(r), Level.FINE),
                    argumentsOf((o, r) -> o.reportConfig(r), Level.CONFIG),
                    argumentsOf((o, r) -> o.reportInfo(r), Level.INFO),
                    argumentsOf((o, r) -> o.reportWarning(r), Level.WARNING),
                    argumentsOf((o, r) -> o.reportSevere(r), Level.SEVERE)
            );
        }

        private static Arguments argumentsOf(ReportResultAction action, Level expectedLevel) {
            return Arguments.of(action, expectedLevel);
        }

        interface ReportLevelAction {
            void apply(ShellReportBuilder builder, String pattern, Object[] parameters);
        }

        interface ReportResultAction {
            Object apply(ShellReportBuilder builder, Object result);
        }
    }
}

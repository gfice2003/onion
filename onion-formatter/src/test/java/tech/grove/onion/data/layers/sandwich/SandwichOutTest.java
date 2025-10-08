package tech.grove.onion.data.layers.sandwich;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.layers.base.LayerBaseTest;
import tech.grove.onion.implementation.core.LoggingCoreApi;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.api.SandwichApi.Token.RESULT_PATTERN;
import static tech.grove.onion.data.layers.sandwich.SandwichOutTest.Data.*;

public class SandwichOutTest extends LayerBaseTest<SandwichOut> {

    interface Data extends LayerBaseTest.Data {
        Object   RESULT   = new Object();
        String   NAME     = "Source";
        Duration DURATION = Duration.ofSeconds(19);
    }

    @Override
    protected SandwichOut createTarget(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        return new SandwichOut(core, handle, level, now);
    }

    @Override
    protected LayerBase.Type layerType() {
        return LayerBase.Type.OUT;
    }

    @Nested
    class SandwichOutFunctionality {

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
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void ctor_default_doesNotInitializeName() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                validator
                        .on(SandwichOut::name).expectingNull()
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void ctor_fromSandwichIn_initializesName() {
            given:
            {
                var source = new SandwichIn(
                        core,
                        HANDLE,
                        LEVEL,
                        NOW::get,
                        NAME);

                target = new SandwichOut(source);
            }
            then:
            {
                validator
                        .on(SandwichOut::name).expecting(NAME)
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
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
                        .on(LayerBase::pattern).expecting(PATTERN)
                        .on(LayerBase::parameters).expecting(PARAMETERS)
                        .validate(target);
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
                        .on(LayerBase::pattern).expecting(RESULT_PATTERN)
                        .on(LayerBase::parameters).expecting(new Object[]{RESULT})
                        .validate(target);
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
                        .on(LayerBase::level).expecting(expectedLevel)
                        .on(LayerBase::pattern).expecting(PATTERN)
                        .on(LayerBase::parameters).expecting(PARAMETERS)
                        .validate(target);
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
                        .on(LayerBase::level).expecting(expectedLevel)
                        .on(LayerBase::pattern).expecting(PATTERN)
                        .on(LayerBase::parameters).expecting(PARAMETERS)
                        .validate(target);
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
                        .on(LayerBase::level).expecting(expectedLevel)
                        .on(LayerBase::pattern).expecting(RESULT_PATTERN)
                        .on(LayerBase::parameters).expecting(new Object[]{RESULT})
                        .validate(target);
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
                        .on(LayerBase::level).expecting(expectedLevel)
                        .on(LayerBase::pattern).expecting(RESULT_PATTERN)
                        .on(LayerBase::parameters).expecting(new Object[]{RESULT})
                        .validate(target);
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

        @Test
        public void close_default_calculatesDuration() {
            given:
            {
                var source = new SandwichIn(
                        core,
                        HANDLE,
                        LEVEL,
                        NOW::get,
                        NAME);

                target = new SandwichOut(source);
            }
            when:
            {
                NOW.set(TIMESTAMP.plus(DURATION));
                target.close();
            }
            then:
            {
                validator
                        .on(LayerBase::duration).expecting(DURATION)
                        .on(LayerBase::timestamp).expecting(TIMESTAMP.plus(DURATION))
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
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
            void apply(SandwichOut sandwichOut, String pattern, Object[] parameters);
        }

        interface ReportResultAction {
            Object apply(SandwichOut sandwichOut, Object result);
        }
    }
}

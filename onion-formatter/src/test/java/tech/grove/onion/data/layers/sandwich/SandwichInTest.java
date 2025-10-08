package tech.grove.onion.data.layers.sandwich;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.FieldsLayer;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.layers.base.LayerBaseTest;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.utils.LayerValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static tech.grove.onion.api.SandwichApi.Token.RESULT_PATTERN;
import static tech.grove.onion.data.layers.sandwich.SandwichInTest.Data.*;
import static tech.grove.onion.tools.Cast.cast;

public class SandwichInTest extends LayerBaseTest<SandwichIn> {

    interface Data extends LayerBaseTest.Data {
        String   NAME          = "SandwichIn";
        Object   RESULT        = new Object();
        Duration DURATION      = Duration.ofSeconds(12);
        Instant  OUT_TIMESTAMP = TIMESTAMP.plus(DURATION);
    }

    @Override
    protected SandwichIn createTarget(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        return new SandwichIn(core, handle, level, now, NAME);
    }

    @Override
    protected LayerBase.Type layerType() {
        return LayerBase.Type.IN;
    }

    @Nested
    class SandwichInFunctionality {

        private FieldsLayer source;
        private FieldsLayer merged;
        private SandwichOut result;

        @Test
        public void ctor_default_initializesTargetName() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                assertEquals(NAME, target.name());
            }
        }

        @Test
        public void ctor_fromLayer_producesCorrectInstance() {
            given:
            {
                source = new FieldsLayer(core, HANDLE, LEVEL);
                target = new SandwichIn(source, NAME);
            }
            then:
            {
                validator
                        .on(SandwichIn::name).expecting(NAME)
                        .on(LayerBase::timestamp).expecting(source.timestamp())
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate(target);
            }
        }

        @Test
        public void ctor_fromLayerWithFields_producesCorrectInstance() {
            given:
            {
                source = cast(new FieldsLayer(core, HANDLE, LEVEL)
                                      .field(FIELD_1.name()).set(FIELD_1.value())
                                      .field(FIELD_2A.name()).set(FIELD_2A.value()));
                target = new SandwichIn(source, NAME);
            }
            then:
            {
                validator
                        .on(SandwichIn::name).expecting(NAME)
                        .on(LayerBase::timestamp).expecting(source.timestamp())
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .on(LayerBase::fields).expecting(List.of(FIELD_1, FIELD_2A))
                        .validate(target);
            }
        }

        @Test
        public void add_default_registersInstanceInCore() {
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

                validator
                        .on(SandwichIn::name).expecting(NAME)
                        .on(LayerBase::pattern).expectingNull()
                        .on(LayerBase::parameters).expectingNull()
                        .validate((SandwichIn) core.layers().getFirst());
            }
        }

        @Test
        public void add_default_returnsValidSandwichOutInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = cast(target.add());
            }
            then:
            {
                resultValidator().validate(result);
            }
        }

        @Test
        public void add_patternAndParameter_registersInstanceInCore() {
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

                validator
                        .on(SandwichIn::name).expecting(NAME)
                        .on(LayerBase::pattern).expecting(PATTERN)
                        .on(LayerBase::parameters).expecting(PARAMETERS)
                        .validate((SandwichIn) core.layers().getFirst());
            }
        }

        @Test
        public void add_patternAndParameter_returnsValidSandwichOutInstance() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                result = cast(target.add(PATTERN, PARAMETERS));
            }
            then:
            {
                resultValidator()
                        .on(LayerBase::pattern).expecting(PATTERN)
                        .on(LayerBase::parameters).expecting(PARAMETERS)
                        .validate(result);
            }
        }

        @Test
        public void tryMerge_skippedOut_returnsNull() {
            given:
            {
                target = createTarget();
                result = cast(target.add(PATTERN, PARAMETERS));
            }
            when:
            {
                result.skip();
            }
            then:
            {
                assertNull(target.tryMerge(result));
            }
        }

        @Test
        public void tryMerge_validOut_returnsExpectedMergedLayer() {
            given:
            {
                target = createTarget();
                result = cast(target.add(PATTERN, PARAMETERS));

                result.report(RESULT);
            }
            when:
            {
                merged = target.tryMerge(result);
            }
            then:
            {
                mergedValidator()
                        .on(LayerBase::pattern).expecting(NAME + SandwichIn.Token.NAME_SEPARATOR + PATTERN + SandwichIn.Token.RESULT + RESULT_PATTERN)
                        .on(LayerBase::parameters).expecting(ArrayUtils.add(PARAMETERS, RESULT))
                        .validate(merged);
            }
        }

        @Test
        public void tryMerge_noPatternAndParametersValidOutWithResult_returnsExpectedMergedLayer() {
            given:
            {
                target = createTarget();
                result = cast(target.add());

                result.report(RESULT);
            }
            when:
            {
                merged = target.tryMerge(result);
            }
            then:
            {
                mergedValidator()
                        .on(LayerBase::pattern).expecting(NAME + SandwichIn.Token.NAME_SEPARATOR + SandwichIn.Token.RESULT + RESULT_PATTERN)
                        .on(LayerBase::parameters).expecting(new Object[]{RESULT})
                        .validate(merged);
            }
        }

        @Test
        public void tryMerge_validOutWithResult_returnsExpectedMergedLayer() {
            given:
            {
                target = createTarget();
                result = cast(target.add(PATTERN, PARAMETERS));

                result.report(RESULT);
            }
            when:
            {
                merged = target.tryMerge(result);
            }
            then:
            {
                mergedValidator()
                        .on(LayerBase::pattern).expecting(NAME + SandwichIn.Token.NAME_SEPARATOR + PATTERN + SandwichIn.Token.RESULT + RESULT_PATTERN)
                        .on(LayerBase::parameters).expecting(ArrayUtils.add(PARAMETERS, RESULT))
                        .validate(merged);
            }
        }

        @Test
        public void tryMerge_outWithDifferentLevel_returnsMergedLayerWithCorrectLevel() {
            given:
            {
                target = createTarget();
                result = cast(target.add(PATTERN, PARAMETERS));

                result.reportSevere(PATTERN, PARAMETERS);
            }
            when:
            {
                merged = target.tryMerge(result);
            }
            then:
            {
                mergedValidator()
                        .on(LayerBase::level).expecting(Level.SEVERE)
                        .validate(merged);
            }
        }

        @Test
        public void tryMerge_outWithDifferentTimestamp_returnsMergedLayerWithCorrectDuration() {
            given:
            {
                target = createTarget();
                result = new SandwichOut(core, HANDLE, LEVEL, NOW::get);

                NOW.set(TIMESTAMP.plus(DURATION));

                target.add(PATTERN, PARAMETERS);
                result.report(PATTERN, PARAMETERS);
                result.close();
            }
            when:
            {
                merged = target.tryMerge(result);
            }
            then:
            {
                mergedValidator()
                        .on(LayerBase::duration).expecting(DURATION)
                        .validate(merged);
            }
        }
    }

    private LayerValidator<FieldsLayer> mergedValidator() {
        return new LayerValidator<FieldsLayer>()
                .on(LayerBase::core).expecting(core)
                .on(LayerBase::handle).expecting(HANDLE)
                .on(LayerBase::level).expecting(LEVEL)
                .on(LayerBase::timestamp).expecting(TIMESTAMP)
                .on(LayerBase::pattern).expecting(NAME + SandwichIn.Token.NAME_SEPARATOR + PATTERN + SandwichIn.Token.RESULT + PATTERN)
                .on(LayerBase::parameters).expecting(ArrayUtils.addAll(PARAMETERS, PARAMETERS));
    }

    private LayerValidator<SandwichOut> resultValidator() {
        return new LayerValidator<SandwichOut>()
                .on(LayerBase::core).expecting(core)
                .on(LayerBase::handle).expecting(HANDLE)
                .on(LayerBase::level).expecting(LEVEL)
                .on(LayerBase::timestamp).expecting(TIMESTAMP)
                .on(LayerBase::pattern).expectingNull()
                .on(LayerBase::parameters).expectingNull();
    }
}

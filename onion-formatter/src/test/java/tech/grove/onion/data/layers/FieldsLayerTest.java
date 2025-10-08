package tech.grove.onion.data.layers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.implementation.core.LoggingCoreApi;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.grove.onion.data.layers.AbstractLayerTest.Data.*;

public class FieldsLayerTest extends AbstractLayerTest<FieldsLayer> {

    @Override
    protected FieldsLayer createTarget(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        return new FieldsLayer(core, handle, level, now);
    }

    @Override
    protected LayerBase.Type layerType() {
        return LayerBase.Type.LAYER;
    }

    @Nested
    class FieldsLayerFunctionality {

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
                assertThrows(ArgumentNullException.class, () -> target.field(null).set(FIELD_1.value()));
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
                target
                        .field(FIELD_NULL.name()).set(FIELD_NULL.value())
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(FIELD_NULL))
                        .validate(target);
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
                target
                        .field(FIELD_1.name()).set(FIELD_1.value())
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(FIELD_1))
                        .validate(target);
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
                target
                        .field(FIELD_1.name()).set(FIELD_1.value())
                        .field(FIELD_2A.name()).set(FIELD_2A.value())
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(FIELD_1, FIELD_2A))
                        .validate(target);
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
                target
                        .field(FIELD_1.name()).set(FIELD_1.value())
                        .field(FIELD_2A.name()).set(FIELD_2A.value())
                        .field(FIELD_2B.name()).set(FIELD_2B.value())
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(FIELD_1, FIELD_2A, FIELD_2B))
                        .validate(target);
            }
        }
    }
}

package tech.grove.onion.data.builders.layer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.utils.Field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static tech.grove.onion.data.builders.layer.LayerBuilderTest.Data.*;

public class FieldsLayerBuilderTest extends LayerBuilderTest<FieldsLayerBuilder> {

    @Override
    protected FieldsLayerBuilder createTarget(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        return new FieldsLayerBuilder(consumer, data);
    }

    @Nested
    class FieldsLayerFunctionality {

        @Test
        public void ctor_default_leavesFieldsByDefault() {
            given:
            {
                target = createTarget();
            }
            then:
            {
                validator.validate(target.data());
            }
        }

        @Test
        public void registerField_nullName_registersFieldWithDefaultName() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.field(null).set(FIELD_1.value());
            }
            then:
            {
                var expected = Map.of(LayerBuilder.Token.NO_NAME, FIELD_1.value());

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
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
                        .field(FIELD_NULL.name()).set(FIELD_NULL.value());
            }
            then:
            {
                var expected = expectedOf(FIELD_NULL);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
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
                        .field(FIELD_1.name()).set(FIELD_1.value());
            }
            then:
            {
                var expected = expectedOf(FIELD_1);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
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
                        .field(FIELD_2A.name()).set(FIELD_2A.value());
            }
            then:
            {
                var expected = expectedOf(FIELD_1, FIELD_2A);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
            }
        }

        @Test
        public void registerField_severalFieldsWithSameName_replacesFieldValueWithSameName() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target
                        .field(FIELD_1.name()).set(FIELD_1.value())
                        .field(FIELD_2A.name()).set(FIELD_2A.value())
                        .field(FIELD_2B.name()).set(FIELD_2B.value());
            }
            then:
            {
                var expected = expectedOf(FIELD_1, FIELD_2B);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
            }
        }


        //-- Так костыльно, потому что конструкция Arrays.stream(fields).collect(Collectors.toMap(Field::name, Field::value)) блокирует null значения
        private Map<String, Object> expectedOf(Field... fields) {
            var result = new HashMap<String, Object>();

            Arrays.stream(fields)
                    .forEach(x -> result.put(x.name(), x.value()));

            return result;
        }
    }
}

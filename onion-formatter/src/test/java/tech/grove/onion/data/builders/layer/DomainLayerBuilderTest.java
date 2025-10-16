package tech.grove.onion.data.builders.layer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.utils.Field;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static tech.grove.onion.data.builders.layer.DomainLayerBuilderTest.Data.*;

public class DomainLayerBuilderTest extends LayerBuilderTest<DomainLayerBuilder<DomainLayerBuilderTest.TestDomainLayer>> {

    interface Data extends LayerBuilderTest.Data {
        String    CUSTOMER_VALUE   = "loc0198723";
        String    NAME_VALUE       = "Name";
        String    NICK_VALUE       = "Nick";
        LocalDate BIRTHDAY_VALUE   = LocalDate.now();
        int       EXPERIENCE_VALUE = 34;

        Field CUSTOMER   = Field.of("Customer", CUSTOMER_VALUE);
        Field BIRTHDAY   = Field.of("Born", BIRTHDAY_VALUE);
        Field EXPERIENCE = Field.of("ExperienceYears", EXPERIENCE_VALUE);
        Field NAME       = Field.of("Name", NAME_VALUE);
        Field NICK       = Field.of("Name", NICK_VALUE);
    }

    @Override
    protected DomainLayerBuilder<TestDomainLayer> createTarget(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        return new DomainLayerBuilder<>(consumer, data, TestDomainLayer.class);
    }

    @Nested
    class DomainLayerFunctionality {

        @Test
        public void ctor_default_leavesFieldsNull() {
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
        public void registerField_nullValue_addsFieldWithNullValue() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.asProxy()
                        .forCustomer(null);
            }
            then:
            {
                var expected = new HashMap<>() {{
                    put(CUSTOMER.name(), null);
                }};

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
                target.asProxy()
                        .forCustomer(CUSTOMER_VALUE);
            }
            then:
            {
                var expected = fieldsOf(CUSTOMER);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
            }
        }

        @Test
        public void registerField_moreFields_addsAllFields() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target.asProxy()
                        .forCustomer(CUSTOMER_VALUE)
                        .bornAt(BIRTHDAY_VALUE)
                        .withName(NAME_VALUE)
                        .withExperienceInYears(EXPERIENCE_VALUE);
            }
            then:
            {
                var expected = fieldsOf(CUSTOMER, NAME, BIRTHDAY, EXPERIENCE);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
            }
        }

        @Test
        public void registerField_oneFieldTwice_addsBothFields() {
            given:
            {
                target = createTarget();
            }
            when:
            {
                target
                        .asProxy()
                        .forCustomer(CUSTOMER_VALUE)
                        .withName(NAME_VALUE)
                        .withName(NICK_VALUE)
                        .bornAt(BIRTHDAY_VALUE);
            }
            then:
            {
                var expected = fieldsOf(CUSTOMER, NICK, BIRTHDAY);

                validator
                        .on(DataCore::fields).expecting(expected.entrySet())
                        .validate(target.data());
            }
        }

        private Map<String, Object> fieldsOf(Field... fields) {
            return Arrays.stream(fields).collect(Collectors.toMap(Field::name, Field::value));
        }
    }

    public interface TestDomainLayer extends Layer {

        TestDomainLayer forCustomer(String customerId);

        TestDomainLayer withName(String name);

        TestDomainLayer bornAt(LocalDate birthday);

        TestDomainLayer withExperienceInYears(int experience);
    }
}
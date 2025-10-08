package tech.grove.onion.data.layers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.api.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.data.fields.Field;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.implementation.core.LoggingCoreApi;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertNull;
import static tech.grove.onion.data.layers.DomainLayerTest.Data.*;

public class DomainLayerTest extends AbstractLayerTest<DomainLayer<DomainLayerTest.TestDomainLayer>> {

    interface Data extends AbstractLayerTest.Data {
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
    protected DomainLayer<DomainLayerTest.TestDomainLayer> createTarget(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now) {
        return new DomainLayer<>(core, handle, level, now, TestDomainLayer.class);
    }

    @Override
    protected LayerBase.Type layerType() {
        return LayerBase.Type.LAYER;
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
                assertNull(target.fields());
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
                        .forCustomer(null)
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(Field.of(CUSTOMER.name(), null)))
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
                target.asProxy()
                        .forCustomer(CUSTOMER_VALUE)
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(CUSTOMER))
                        .validate(target);
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
                        .withExperienceInYears(EXPERIENCE_VALUE)
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(CUSTOMER, BIRTHDAY, EXPERIENCE))
                        .validate(target);
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
                        .bornAt(BIRTHDAY_VALUE)
                        .add(PATTERN, PARAMETERS);
            }
            then:
            {
                validator
                        .on(AbstractLayer::fields).expecting(List.of(CUSTOMER, NICK, NAME, BIRTHDAY))
                        .validate(target);
            }
        }
    }

    public interface TestDomainLayer extends Layer {

        TestDomainLayer forCustomer(String customerId);

        TestDomainLayer withName(String name);

        TestDomainLayer bornAt(LocalDate birthday);

        TestDomainLayer withExperienceInYears(int experience);
    }
}
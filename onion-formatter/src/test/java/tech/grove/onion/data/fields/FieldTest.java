package tech.grove.onion.data.fields;

import org.junit.jupiter.api.Test;
import tech.grove.onion.data.layers.FieldsLayer;
import tech.grove.onion.exceptions.ArgumentNullException;

import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.data.fields.FieldTest.Data.*;

public class FieldTest {

    interface Data {
        String NAME            = "field";
        int    VALUE           = 56;
        String FORMATTED_VALUE = "_56_";
    }

    private Field target;


    @Test
    public void test() {
        var map = new HashMap<Function<FieldsLayer, Level>, Integer>();

        map.put(FieldsLayer::level,3);
        map.put(FieldsLayer::level,4);

        System.out.println(map);
    }

    @Test
    public void ctor_nullName_throwsIllegalArgumentException() {
        then:
        {
            assertThrows(ArgumentNullException.class, () -> new Field(null, 5, Object::toString));
        }
    }

    @Test
    public void name_validNamePassed_returnsValidName() {
        given:
        {
            target = new Field(NAME, VALUE, null);
        }
        then:
        {
            assertEquals(NAME, target.name());
        }
    }

    @Test
    public void value_nullValuePassed_returnsNullToken() {
        given:
        {
            target = new Field(NAME, null, null);
        }
        then:
        {
            assertEquals(Field.Default.NULL, target.value());
        }
    }

    @Test
    public void value_defaultFormatter_returnsValueToString() {
        given:
        {
            target = new Field(NAME, VALUE, null);
        }
        then:
        {
            assertEquals(String.valueOf(VALUE), target.value());
        }
    }

    @Test
    public void value_customFormatter_returnsFormattedValue() {
        given:
        {
            target = new Field(NAME, VALUE, x -> FORMATTED_VALUE);
        }
        then:
        {
            assertEquals(FORMATTED_VALUE, target.value());
        }
    }
}

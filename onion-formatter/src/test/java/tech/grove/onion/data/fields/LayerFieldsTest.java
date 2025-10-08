package tech.grove.onion.data.fields;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;
import static tech.grove.onion.data.fields.LayerFieldsTest.Data.*;

public class LayerFieldsTest {

    interface Data {
        List<Integer>             VALUES       = List.of(1, 2, 3, 4, 5);
        Function<Object, String>  FORMATTER    = x -> String.valueOf((int) x * 10);
        Function<Integer, String> NAME_FACTORY = "Field #%s"::formatted;
    }

    private LayerFields result;

    @Test
    public void ctor_default_returnsEmptyResult() {
        given:
        {
            result = new LayerFields();
        }
        then:
        {
            validate(result);
        }
    }

    @Test
    public void add_defaultFormatter_returnsCorrectResult() {
        given:
        {
            result = new LayerFields();
        }
        when:
        {
            VALUES.forEach(value -> result.add(NAME_FACTORY.apply(value), value));
        }
        then:
        {
            var expected = VALUES.stream()
                    .map(value ->
                                 new Field(
                                         NAME_FACTORY.apply(value),
                                         value,
                                         null))
                    .toArray(Field[]::new);

            validate(result, expected);
        }
    }

    @Test
    public void add_customFormatter_returnsCorrectResult() {
        given:
        {
            result = new LayerFields();
        }
        when:
        {
            VALUES.forEach(value -> result.add(NAME_FACTORY.apply(value), value, FORMATTER));
        }
        then:
        {
            var expected = VALUES.stream()
                    .map(value ->
                                 new Field(
                                         NAME_FACTORY.apply(value),
                                         value,
                                         FORMATTER))
                    .toArray(Field[]::new);

            validate(result, expected);
        }
    }

    private void validate(LayerFields result, Field... expected) {
        var set = Sets.newHashSet(expected);

        for (var field : result) {
            if (!set.remove(field)) {
                fail("%s is not expected".formatted(field));
            }
        }

        for (var missing : set) {
            fail("%s is missing".formatted(missing));
        }
    }
}

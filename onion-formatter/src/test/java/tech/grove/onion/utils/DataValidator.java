package tech.grove.onion.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.tools.Builder;
import tech.grove.onion.tools.SerializableGetter;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.grove.onion.tools.SerializableGetter.getMethodName;

public class DataValidator extends Builder<DataValidator> implements Cloneable {

    private final Map<String, Rule> rules = Maps.newHashMap();

    public void validate(DataCore instance) {
        rules.values().forEach(rule -> rule.validate(instance));
    }

    public ExpectedValueSetter on(SerializableGetter<DataCore, Object> getter) {
        return value -> setAndContinue(getter, get -> addRule(get, value), NullAction.THROW);
    }

    private void addRule(SerializableGetter<DataCore, Object> getter, Object expected) {
        var rule = new Rule(getter, expected);
        var name = getMethodName(getter);

        rules.put(name, rule);
    }

    public interface ExpectedValueSetter {

        default DataValidator expectingNull() {
            return expecting(null);
        }

        DataValidator expecting(Object expected);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public record Rule(SerializableGetter<DataCore, Object> getter, Object expected) implements Cloneable {

        public void validate(DataCore instance) {
            var actual = getter.apply(instance);

            if (actual instanceof Iterable<?> actualValues && expected instanceof Iterable<?> expectedValues) {
                validateIterable(expectedValues, actualValues);
            }
            if (actual instanceof Map<?, ?> actualValues && expected instanceof Map<?, ?> expectedValues) {
                validateMap(expectedValues, actualValues);
            } else if (isArray(expected) && isArray(actual)) {
                validateArray((Object[]) expected, (Object[]) actual);
            } else {
                validateValue(expected, actual);
            }
        }

        private boolean isArray(Object value) {
            return Optional.ofNullable(value).map(x -> x.getClass().isArray()).orElse(false);
        }

        private void validateValue(Object expected, Object actual) {
            assertEquals(expected, actual, this::failureMessage);
        }

        private void validateArray(Object[] expected, Object[] actual) {
            assertEquals(expected.length, actual.length);

            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], actual[i], "Wrong element at %s".formatted(i));
            }
        }

        private void validateIterable(Iterable<?> expected, Iterable<?> actual) {
            var set = Sets.newHashSet(expected);

            for (var element : actual) {
                if (!set.remove(element)) {
                    fail(failureMessage() + " Unexpected element %s".formatted(element));
                }
            }

            for (var element : set) {
                fail(failureMessage() + " Missing element %s".formatted(element));
            }
        }

        private void validateMap(Map<?, ?> expected, Map<?, ?> actual) {
            var map = Maps.newHashMap(expected);

            for (var entry : actual.entrySet()) {
                assertEquals(expected.get(entry.getKey()), map.remove(entry.getKey()));
            }

            for (var entry : map.entrySet()) {
                fail(failureMessage() + " Missing entry %s".formatted(entry));
            }
        }

        private String failureMessage() {
            return "Validation failed for field: %s.".formatted(getMethodName(getter));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}

package tech.grove.onion.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import tech.grove.onion.tools.Builder;
import tech.grove.onion.tools.SerializableGetter;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.grove.onion.tools.SerializableGetter.getMethodName;

public class LayerValidator<T> extends Builder<LayerValidator<T>> {

    private final Map<String, Rule<T>> rules = Maps.newHashMap();

    public void validate(T instance) {
        rules.values().forEach(rule -> rule.validate(instance));
    }

    public ExpectedValueSetter<T> on(SerializableGetter<T, Object> getter) {
        return value -> setAndContinue(getter, get -> addRule(get, value), NullAction.THROW);
    }

    private void addRule(SerializableGetter<T, Object> getter, Object expected) {
        var rule = new Rule<>(getter, expected);
        var name = getMethodName(getter);

        rules.put(name, rule);
    }

    public interface ExpectedValueSetter<T> {

        default LayerValidator<T> expectingNull() {
            return expecting(null);
        }

        LayerValidator<T> expecting(Object expected);
    }

    private record Rule<T>(SerializableGetter<T, Object> getter, Object expected) {

        public void validate(T instance) {
            var actual = getter.apply(instance);

            if (actual instanceof Iterable<?> actualValues && expected instanceof Iterable<?> expectedValues) {
                validateIterable(expectedValues, actualValues);
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

        private String failureMessage() {
            return "Validation failed for field: %s.".formatted(getMethodName(getter));
        }
    }
}

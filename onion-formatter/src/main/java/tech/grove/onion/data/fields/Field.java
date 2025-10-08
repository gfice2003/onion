package tech.grove.onion.data.fields;

import com.google.common.base.Objects;
import tech.grove.onion.exceptions.ArgumentNullException;

import java.util.Optional;
import java.util.function.Function;

public class Field {

    public static final class Default {
        public static final String                   NULL      = "null";
        public static final Function<Object, String> FORMATTER = x -> Optional.ofNullable(x).map(Object::toString).orElse(NULL);
    }

    private final String                   name;
    private final Object                   value;
    private final Function<Object, String> formatter;

    public Field(String name, Object value, Function<Object, String> formatter) {
        this.name      = Optional.ofNullable(name).orElseThrow(() -> new ArgumentNullException("name"));
        this.value     = value;
        this.formatter = Optional.ofNullable(formatter).orElse(Default.FORMATTER);
    }

    public String name() {
        return name;
    }

    public String value() {
        return Optional.ofNullable(value).map(formatter).orElse(Default.NULL);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equal(name, field.name) && Objects.equal(value(), field.value());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, value());
    }

    @Override
    public String toString() {
        return "(Field) Name: %s, Value: %s".formatted(name, value());
    }

    public static Field of(String name, Object value) {
        return new Field(name, value, null);
    }
}

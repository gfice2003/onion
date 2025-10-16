package tech.grove.onion.utils;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;

public record Field(String name, Object value) implements Map.Entry<String, Object> {

    public static Field of(String name, Object value) {
        return new Field(name, value);
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Object setValue(Object value) {
        throw new NotImplementedException();
    }
}

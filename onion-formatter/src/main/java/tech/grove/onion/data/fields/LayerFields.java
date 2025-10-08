package tech.grove.onion.data.fields;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class LayerFields implements Iterable<Field> {

    private final List<Field> fields = Lists.newArrayList();


    public void add(String name, Object value) {
        add(name, value, null);
    }

    public void add(String name, Object value, Function<Object, String> formatter) {
        fields.add(new Field(name, value, formatter));
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.iterator();
    }
}

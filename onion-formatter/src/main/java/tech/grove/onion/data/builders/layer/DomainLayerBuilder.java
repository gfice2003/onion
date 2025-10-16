package tech.grove.onion.data.builders.layer;

import tech.grove.onion.api.layer.Layer;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.tools.FieldNameHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import static tech.grove.onion.tools.Cast.cast;

public class DomainLayerBuilder<Domain extends Layer> extends LayerBuilder<DomainLayerBuilder<Domain>> implements InvocationHandler {

    private final Class<Domain> domainClass;

    public DomainLayerBuilder(Consumer<? super BaseBuilder<?>> consumer, DataCore data, Class<Domain> domainClass) {
        super(consumer, data);
        this.domainClass = domainClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(domainClass)) {

            var field = FieldNameHelper.getFieldNameFor(method);
            var value = extractFieldValue(args);

            data.withField(field, value);

            return proxy;
        } else {
            return method.invoke(this, args);
        }
    }

    public Domain asProxy() {
        return cast(Proxy.newProxyInstance(domainClass.getClassLoader(), new Class<?>[]{domainClass}, this));
    }

    private Object extractFieldValue(Object[] args) {
        Object result = null;

        if (args != null && args.length > 0) {
            result = args[0];
        }

        return result;
    }
}

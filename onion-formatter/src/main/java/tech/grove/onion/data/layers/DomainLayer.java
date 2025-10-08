package tech.grove.onion.data.layers;

import tech.grove.onion.api.Layer;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.implementation.core.LoggingCoreApi;
import tech.grove.onion.tools.FieldNameHelper;
import tech.grove.onion.tools.ToStringBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.logging.Level;

import static tech.grove.onion.tools.Cast.cast;

public class DomainLayer<Domain extends Layer> extends AbstractLayer<DomainLayer<Domain>> implements InvocationHandler {

    private final Class<Domain> domainClass;

    public DomainLayer(LoggingCoreApi core, Handle handle, Level level, Class<Domain> domainClass) {
        super(core, handle, level);

        this.domainClass = domainClass;
    }

    DomainLayer(LoggingCoreApi core, Handle handle, Level level, Supplier<Instant> now, Class<Domain> domainClass) {
        super(core, handle, level, now);

        this.domainClass = domainClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(domainClass)) {

            var field = FieldNameHelper.getFieldNameFor(method);
            var value = extractFieldValue(args);

            registerField(field, value);

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

    @Override
    protected void toStringHandler(ToStringBuilder builder) {
        super.toStringHandler(builder);
        builder.property("Domain").set(domainClass);
    }
}

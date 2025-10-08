package tech.grove.onion.utils;

import tech.grove.onion.tools.StackHelper;

import java.util.stream.IntStream;

public class StackUtils {

    public static StackWalker.StackFrame[] stackOfSize(int size) {
        return IntStream.range(0, size)
                .boxed()
                .map(x -> frameFor(StackHelper.class, "Method " + x))
                .toArray(StackWalker.StackFrame[]::new);
    }

    public static StackWalker.StackFrame frameFor(Class<?> expectedClass, String method) {
        return new TestStackFrame(expectedClass.getName(), method, 0);
    }
}

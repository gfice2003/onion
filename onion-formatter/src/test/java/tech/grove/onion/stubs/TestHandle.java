package tech.grove.onion.stubs;

import tech.grove.onion.data.context.Handle;

public record TestHandle(String className, String methodName, int lineNumber) implements Handle {
}

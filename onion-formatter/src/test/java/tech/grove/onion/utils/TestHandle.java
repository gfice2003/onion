package tech.grove.onion.utils;

import tech.grove.onion.data.context.Handle;

public record TestHandle(String className, String methodName, int lineNumber) implements Handle {
}

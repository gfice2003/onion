package tech.grove.onion.utils;

import tech.grove.onion.tools.StackHelperApi;

public record TestStackHelper(StackWalker.StackFrame[] stack) implements StackHelperApi {

    @Override
    public StackHelperApi withKnown(Class<?> knownClass) {
        return this;
    }

    @Override
    public StackTraceElement getFirstUnknown() {
        return stack[0].toStackTraceElement();
    }

    @Override
    public StackWalker.StackFrame getFirstUnknownFrame() {
        return stack[0];
    }

    @Override
    public StackWalker.StackFrame[] getUnknown() {
        return stack;
    }
}
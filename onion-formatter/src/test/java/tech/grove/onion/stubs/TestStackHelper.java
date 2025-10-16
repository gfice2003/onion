package tech.grove.onion.stubs;

import tech.grove.onion.tools.stack.StackHelperApi;

public record TestStackHelper(StackWalker.StackFrame[] stack) implements StackHelperApi {

    @Override
    public StackHelperApi withKnown(Class<?> knownClass) {
        return this;
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
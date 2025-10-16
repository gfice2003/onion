package tech.grove.onion.tools.stack;

import tech.grove.onion.data.stack.StackMode;

public interface StackHelperApi {

    StackHelperApi withKnown(Class<?> knownClass);

    StackWalker.StackFrame getFirstUnknownFrame();

    StackWalker.StackFrame[] getUnknown();

    interface ModeSetter<R> {
        R mode(StackMode mode);
    }
}

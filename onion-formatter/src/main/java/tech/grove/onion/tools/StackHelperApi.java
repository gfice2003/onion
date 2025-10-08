package tech.grove.onion.tools;

import tech.grove.onion.data.stack.StackMode;

public interface StackHelperApi {

    StackHelperApi withKnown(Class<?> knownClass);

    //-- TODO: Remove
    StackTraceElement getFirstUnknown();

    StackWalker.StackFrame getFirstUnknownFrame();

    StackWalker.StackFrame[] getUnknown();

    interface ModeSetter<R> {
        R mode(StackMode mode);
    }
}

package tech.grove.onion.data.context;

public interface Handle {

    String className();

    String methodName();

    int lineNumber();


    static Handle of(StackWalker.StackFrame element) {
        return new StackFrameHandle(element);
    }
}

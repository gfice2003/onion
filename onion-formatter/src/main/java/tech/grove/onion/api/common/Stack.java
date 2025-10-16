package tech.grove.onion.api.common;

import tech.grove.onion.data.stack.StackMode;

public interface Stack<T> {

    default T putStack() {
        return putStack(StackMode.FAIR);
    }

    T putStack(StackMode mode);
}
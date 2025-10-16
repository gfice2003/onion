package tech.grove.onion.api.common;

import tech.grove.onion.data.stack.StackMode;

public interface ExceptionStack<T> {

    default T noStack() {
        return stackMode(StackMode.NONE);
    }

    default T fullStack() {
        return stackMode(StackMode.FULL);
    }

    T stackMode(StackMode mode);
}
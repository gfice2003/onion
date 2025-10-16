package tech.grove.onion.api.common;

public interface Exception<T, S extends ExceptionStack<T>> {

    S exception(Throwable exception);
}
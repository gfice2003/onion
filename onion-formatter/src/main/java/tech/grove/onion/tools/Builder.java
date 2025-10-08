package tech.grove.onion.tools;

import tech.grove.onion.exceptions.ArgumentNullException;

import java.util.function.Consumer;

import static tech.grove.onion.tools.Cast.cast;

public abstract class Builder<API extends Builder<API>> {

    protected <T> API setAndContinue(T value, Consumer<T> setter) {
        return setAndContinue(value, setter, NullAction.ACCEPT);
    }

    protected <T> API setAndContinue(T value, Consumer<T> setter, NullAction nullAction) {

        if (setter != null) {

            var set = true;

            if (value == null) {
                switch (nullAction) {
                    case THROW -> throw new ArgumentNullException("value");
                    case SKIP -> set = false;
                }
            }

            if (set) {
                setter.accept(value);
            }
        }

        return cast(this);
    }


    protected enum NullAction {
        ACCEPT,
        THROW,
        SKIP
    }
}
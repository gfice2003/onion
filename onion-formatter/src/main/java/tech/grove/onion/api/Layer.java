package tech.grove.onion.api;

public interface Layer extends SandwichApi.NameSetter, LayerApi.StackFormatter, LayerApi.Adder {

    LayerApi.ExceptionFormatter exception(Throwable exception);
}

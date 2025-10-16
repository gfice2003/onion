package tech.grove.onion.compiler;

import tech.grove.onion.tools.chained.ChainedTransformer;

public interface CompilerApi<T> extends ChainedTransformer<Compilable, Compiled<T>> {

}

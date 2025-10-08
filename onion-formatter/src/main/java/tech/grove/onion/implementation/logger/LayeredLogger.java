package tech.grove.onion.implementation.logger;

import tech.grove.onion.compiled.CompiledLayerLogger;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.implementation.compilers.LayerStringCompiler;
import tech.grove.onion.implementation.core.LayerConsumer;
import tech.grove.onion.implementation.handlers.DepthPreprocessor;

import java.util.Stack;
import java.util.function.Function;
import java.util.logging.Level;

public class LayeredLogger implements LayeredLoggerApi {

    private final Stack<LayerConsumer> preprocessors = new Stack<>();

    private final CompiledLayerLogger<String> logger;
    private final LayerStringCompiler         compiler;

    public LayeredLogger(CompiledLayerLogger<String> logger) {

        this.logger   = logger;
        this.compiler = new LayerStringCompiler(LayerStringCompiler.Configuration.DEFAULT); //-- TODO: move to external  config

        initializePreprocessors();
    }

    @Override
    public String name() {
        return logger.name();
    }

    @Override
    public boolean isEnabled(Level level) {
        return logger.isEnabled(level);
    }

    private void initializePreprocessors() {
        registerPreprocessor(DepthPreprocessor::new);
    }

    private void registerPreprocessor(Function<LayerConsumer, LayerConsumer> factory) {
        LayerConsumer parent = this::acceptPreprocessed;

        if (!preprocessors.isEmpty()) {
            parent = preprocessors.peek();
        }

        preprocessors.add(factory.apply(parent));
    }

    @Override
    public void accept(LayerBase<? extends LayerBase<?>> layer) {
        preprocessors.peek().accept(layer);
    }

    private void acceptPreprocessed(LayerBase<? extends LayerBase<?>> layer) {
        logger.write(compiler.compile(layer));
    }
}

package tech.grove.onion.log.processor;

import tech.grove.onion.backend.LoggingBackend;
import tech.grove.onion.backend.LoggingBackendApi;
import tech.grove.onion.backend.logger.LoggerInfo;
import tech.grove.onion.compiler.CompilerApi;
import tech.grove.onion.compiler.StringCompiler;
import tech.grove.onion.data.DataPreprocessor;
import tech.grove.onion.data.builders.base.BaseBuilder;

public class LogProcessor implements LogProcessorApi {

    private final DataPreprocessor  preprocessor;
    private final LoggingBackendApi backend;

    public LogProcessor() {
        this(new DataPreprocessor(), new StringCompiler(), new LoggingBackend());
    }

    LogProcessor(DataPreprocessor preprocessor, CompilerApi<String> compiler, LoggingBackendApi backend) {

        preprocessor.bindConsumer(builder -> compiler.accept(builder.data()));
        compiler.bindConsumer(backend::write);

        this.preprocessor = preprocessor;
        this.backend      = backend;
    }

    @Override
    public LoggerInfo loggerFor(String logger) {
        return backend.loggerFor(logger);
    }

    @Override
    public void accept(BaseBuilder<?> builder) {
        preprocessor.accept(builder);
    }
}
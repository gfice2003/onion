package tech.grove.onion.implementation.compilers;

import com.google.common.base.Strings;
import tech.grove.onion.compiled.CompiledLayer;
import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.layers.AbstractLayer;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.layers.sandwich.SandwichIn;
import tech.grove.onion.data.layers.sandwich.SandwichOut;
import tech.grove.onion.data.stack.StackInfo;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiPredicate;

import static tech.grove.onion.implementation.compilers.LayerStringCompiler.Token.*;
import static tech.grove.onion.tools.Cast.cast;

public class LayerStringCompiler implements LayerCompilerApi<String> {

    interface Token {
        String OFFSET                = "   ";
        String SEPARATOR             = ": ";
        String SPACE                 = " ";
        String TEMPORAL_SEPARATOR    = "-";
        String TEMPORAL_START        = "[";
        String TEMPORAL_STOP         = "]";
        String FIELDS_START          = "(";
        String FIELDS_STOP           = ")";
        String FIELDS_SEPARATOR      = ",";
        String FIELD_VALUE_SEPARATOR = "=";
        String SANDWICH_IN           = "{+}";
        String SANDWICH_OUT          = "{-}";
        String STACK_DOTS            = "...";
        String STACK_MORE            = " more";
        String EXCEPTION             = "Exception: ";
        String EXCEPTION_CAUSE       = "Caused by: ";
        String NULL                  = "null";
    }

    private final LayerCompiler<? extends AbstractLayer<?>> layerCompiler;
    private final SandwichInCompiler                        inCompiler;
    private final SandwichOutCompiler                       outCompiler;

    public LayerStringCompiler(Configuration configuration) {
        this.layerCompiler = new LayerCompiler<>(configuration);
        this.inCompiler    = new SandwichInCompiler(configuration);
        this.outCompiler   = new SandwichOutCompiler(configuration);
    }

    @Override
    public CompiledLayer<String> compile(LayerBase<?> layer) {
        return new CompiledLayer<>(layer.level(), compileLayer(layer));
    }

    private String compileLayer(LayerBase<?> layer) {
        return switch (layer) {
            case SandwichIn in -> inCompiler.compile(in);
            case SandwichOut out -> outCompiler.compile(out);
            default -> layerCompiler.compile(cast(layer));
        };
    }

    private final static class LayerCompiler<L extends AbstractLayer<L>> extends Compiler<L> {

        private LayerCompiler(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected List<BiPredicate<StringBuilder, L>> initializePipeline() {
            return List.of(
                    this::printOffset,
                    this::printMessage,
                    this::printFields,
                    this::printTemporal,
                    this::printStack,
                    this::printException
            );
        }

        private boolean printStack(StringBuilder builder, L layer) {
            return printStack(builder, layer.stack());
        }

        private boolean printStack(StringBuilder builder, StackInfo stack) {
            if (stack != null) {
                for (var frame : stack.elements()) {
                    builder.append(System.lineSeparator()).append(frame);
                }

                var more = stack.size() - stack.elements().length;

                if (more > 0) {
                    builder.append(System.lineSeparator())
                            .append(STACK_DOTS)
                            .append(more)
                            .append(STACK_MORE);
                }

                return true;
            }
            return false;
        }

        private boolean printException(StringBuilder builder, L layer) {
            return printException(builder, layer.exception(), false);
        }

        private boolean printException(StringBuilder builder, ExceptionInfo exception, boolean cause) {
            if (exception != null) {
                if (cause) {
                    builder.append(System.lineSeparator()).append(EXCEPTION_CAUSE);
                } else {
                    builder.append(System.lineSeparator()).append(EXCEPTION);
                }

                builder.append(exception.type()).append(SEPARATOR).append(exception.message());

                printStack(builder, exception.stack());
                printException(builder, exception.causedBy(), true);

                return true;
            }
            return false;
        }
    }

    private final static class SandwichOutCompiler extends Compiler<SandwichOut> {

        private SandwichOutCompiler(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected List<BiPredicate<StringBuilder, SandwichOut>> initializePipeline() {
            return List.of(
                    this::printOffset,
                    this::printSandwichOut,
                    this::printMessage,
                    this::printTemporal
            );
        }

        private boolean printSandwichOut(StringBuilder builder, SandwichOut ignored) {
            if (configuration().printSandwichMarks) {
                builder.append(SANDWICH_OUT);
                return true;
            }
            return false;
        }
    }

    private final static class SandwichInCompiler extends Compiler<SandwichIn> {

        private SandwichInCompiler(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected List<BiPredicate<StringBuilder, SandwichIn>> initializePipeline() {
            return List.of(
                    this::printOffset,
                    this::printSandwichIn,
                    this::printName,
                    this::printMessage,
                    this::printFields,
                    this::printTemporal
            );
        }

        private boolean printName(StringBuilder builder, SandwichIn layer) {
            if (!Strings.isNullOrEmpty(layer.name())) {
                builder.append(layer.name()).append(SEPARATOR);
                return true;
            }
            return false;
        }

        private boolean printSandwichIn(StringBuilder builder, SandwichIn ignored) {
            if (configuration().printSandwichMarks) {
                builder.append(SANDWICH_IN);
                return true;
            }
            return false;
        }
    }

    private abstract static class Compiler<L extends LayerBase<L>> {

        private final Configuration                       configuration;
        private final List<BiPredicate<StringBuilder, L>> pipeline;

        private Compiler(Configuration configuration) {
            this.configuration = configuration;
            this.pipeline      = initializePipeline();
        }

        protected abstract List<BiPredicate<StringBuilder, L>> initializePipeline();

        protected Configuration configuration() {
            return configuration;
        }

        public String compile(L layer) {

            if (layer == null) {
                return NULL;
            }

            var result = new StringBuilder();

            for (var step : pipeline) {
                if (step.test(result, layer)) {
                    result.append(SPACE);
                }
            }

            return result.toString();
        }

        protected boolean printOffset(StringBuilder builder, L layer) {
            if (layer.depth() > 0 && configuration.printOffsets) {
                builder.append(OFFSET.repeat(layer.depth()));
                return true;
            }
            return false;

        }

        protected boolean printMessage(StringBuilder builder, L layer) {
            var result = true;

            if (layer.pattern() != null && layer.parameters() != null) {
                builder.append(layer.pattern().formatted(layer.parameters()));
            } else if (layer.pattern() != null) {
                builder.append(layer.pattern());
            } else if (layer.parameters() != null) {
                for (int i = 0; i < layer.parameters().length; i++) {
                    if (i > 0) {
                        builder.append(SPACE);
                    }
                    builder.append(layer.parameters()[i]);
                }
            } else {
                result = false;
            }

            return result;
        }

        protected boolean printFields(StringBuilder builder, L layer) {
            var firstField = true;

            if (layer.fields() != null) {

                for (var field : layer.fields()) {
                    if (firstField) {
                        builder.append(FIELDS_START);
                        firstField = false;
                    } else {
                        builder.append(FIELDS_SEPARATOR);
                    }
                    builder.append(field.name()).append(FIELD_VALUE_SEPARATOR).append(field.value());
                }

                if (!firstField) {
                    builder.append(FIELDS_STOP);
                }
            }

            return !firstField;
        }

        protected boolean printTemporal(StringBuilder builder, L layer) {
            var initiated = false;

            if (layer.timestamp() != null && configuration.printTimestamp()) {
                builder.append(TEMPORAL_START).append(configuration.timestampFormater().format(layer.timestamp()));
                initiated = true;
            }

            if (layer.duration() != null && configuration.printDuration) {
                if (initiated) {
                    builder.append(TEMPORAL_SEPARATOR);
                } else {
                    builder.append(TEMPORAL_START);
                }
                builder.append(layer.duration()).append(TEMPORAL_STOP);
            } else if (initiated) {
                builder.append(TEMPORAL_STOP);
            }

            return initiated;
        }
    }

    public record Configuration(boolean printOffsets,
                                boolean printSandwichMarks,
                                DateTimeFormatter timestampFormater,
                                boolean printDuration) {

        public boolean printTimestamp() {
            return timestampFormater != null;
        }

        public Configuration(boolean printOffsets,
                             boolean printSandwichMarks,
                             String timestampFormat,
                             boolean printDuration) {
            this(printOffsets,
                 printSandwichMarks,
                 DateTimeFormatter.ofPattern(timestampFormat).withZone(ZoneId.systemDefault()),
                 printDuration);
        }

        public static final Configuration DEFAULT = new Configuration(
                true,
                true,
                "mm:ss.SSS",
                true);
    }
}

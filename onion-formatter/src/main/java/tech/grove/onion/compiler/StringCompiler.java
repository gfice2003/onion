package tech.grove.onion.compiler;

import com.google.common.base.Strings;

import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackInfo;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.tools.chained.BaseChainedTransformer;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import static tech.grove.onion.compiler.StringCompiler.Token.*;

public class StringCompiler extends BaseChainedTransformer<Compilable, Compiled<String>> implements CompilerApi<String> {

    interface Token {
        String OFFSET                = "   ";
        String SEPARATOR             = ":";
        String SPACE                 = " ";
        String DURATION_START        = "[";
        String DURATION_STOP         = "]";
        String FIELDS_START          = "(";
        String FIELDS_STOP           = ")";
        String FIELDS_SEPARATOR      = ",";
        String FIELD_VALUE_SEPARATOR = "=";
        String STACK_DOTS            = "...";
        String STACK_MORE            = " more";
        String EXCEPTION             = "Exception: ";
        String EXCEPTION_CAUSE       = "Caused by: ";
    }

    private final Configuration                                configuration;
    private final List<BiPredicate<StringBuilder, Compilable>> pipeline = List.of(
            this::printOffset,
            this::printIcon,
            this::printName,
            this::printMessage,
            this::printFields,
            this::printDuration,
            this::printStack,
            this::printException
    );

    public StringCompiler() {
        this(null);
    }

    public StringCompiler(Configuration configuration) {
        this.configuration = Optional.ofNullable(configuration)
                .orElse(Configuration.DEFAULT);
    }

    @Override
    protected Compiled<String> transform(Compilable input) {

        var result = new StringBuilder();

        for (var step : pipeline) {
            if (step.test(result, input)) {
                result.append(SPACE);
            }
        }

        return new Compiled<>(input.logger(), input.level(), result.toString());
    }

    protected boolean printOffset(StringBuilder builder, Compilable entry) {
        if (entry.depth() > 0 && configuration.printOffsets) {
            builder.append(OFFSET.repeat(entry.depth()));
        }
        return false;
    }

    protected boolean printIcon(StringBuilder builder, Compilable entry) {
        if (entry.icon() != null && configuration.printOffsets) {
            builder.append(entry.icon());
            return true;
        }
        return false;
    }

    private boolean printName(StringBuilder builder, Compilable entry) {
        if (!Strings.isNullOrEmpty(entry.name())) {
            builder.append(entry.name()).append(SEPARATOR);
            return true;
        }
        return false;
    }

    private boolean printMessage(StringBuilder builder, Compilable entry) {
        if (!Strings.isNullOrEmpty(entry.message())) {
            builder.append(entry.message());
            return true;
        }
        return false;
    }

    protected boolean printFields(StringBuilder builder, Compilable entry) {
        var firstField = true;

        if (entry.fields() != null) {

            for (var field : entry.fields()) {
                if (firstField) {
                    builder.append(FIELDS_START);
                    firstField = false;
                } else {
                    builder.append(FIELDS_SEPARATOR);
                }
                builder.append(field.getKey()).append(FIELD_VALUE_SEPARATOR).append(field.getValue());
            }

            if (!firstField) {
                builder.append(FIELDS_STOP);
            }
        }

        return !firstField;
    }

    protected boolean printDuration(StringBuilder builder, Compilable entry) {
        var result = false;

        if (entry.duration() != null && configuration.printDuration) {
            builder.append(DURATION_START).append(entry.duration()).append(DURATION_STOP);
            result = true;
        }

        return result;
    }

    private boolean printStack(StringBuilder builder, Compilable entry) {
        return printStack(builder, entry.stack());
    }

    private boolean printStack(StringBuilder builder, StackInfo stack) {
        if (stack != null && stack.mode() != StackMode.NONE) {
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
        }
        return false;
    }

    private boolean printException(StringBuilder builder, Compilable entry) {
        return printException(builder, entry.exception(), false);
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
        }
        return false;
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

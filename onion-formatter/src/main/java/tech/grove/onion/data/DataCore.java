package tech.grove.onion.data;

import com.google.common.collect.Maps;
import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackInfo;
import tech.grove.onion.data.stack.StackMode;
import tech.grove.onion.compiler.Compilable;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.tools.stack.StackHelper;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DataCore implements Compilable {

    interface Token {
        String PARAMETER_SEPARATOR = ",";
    }

    public interface Default {
        Level     LEVEL      = Level.INFO;
        int       DEPTH      = 0;
        StackMode STACK_MODE = StackMode.FAIR;
    }

    private final String logger;

    private Level                    level      = Default.LEVEL;
    private int                      depth      = Default.DEPTH;
    private String                   icon       = null;
    private String                   name       = null;
    private String                   pattern    = null;
    private Object[]                 parameters = null;
    private Map<String, Object>      fields     = null;
    private Duration                 duration   = null;
    private StackWalker.StackFrame[] stack      = null;
    private Throwable                exception  = null;
    private StackMode                stackMode  = Default.STACK_MODE;

    public DataCore(String logger) {
        this.logger = Optional.ofNullable(logger)
                .orElseThrow(() -> new ArgumentNullException("logger"));
    }

    //<editor-fold desc="Compilable implementation">

    @Override
    public String logger() {
        return logger;
    }

    @Override
    public Level level() {
        return level;
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public String icon() {
        return icon;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String message() {
        String result = pattern;

        if (pattern != null && parameters != null) {
            return pattern.formatted(parameters);
        } else if (pattern != null) {
            return pattern;
        } else if (parameters != null) {
            return Arrays.stream(parameters)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining(Token.PARAMETER_SEPARATOR));
        }

        return result;
    }

    @Override
    public Iterable<Map.Entry<String, Object>> fields() {
        return Optional.ofNullable(fields)
                .map(Map::entrySet)
                .orElse(Set.of());
    }

    @Override
    public StackInfo stack() {
        return StackHelper.toInfo(stack).mode(stackMode);
    }

    @Override
    public ExceptionInfo exception() {
        return ExceptionInfo.of(exception).stackMode(stackMode).build();
    }

    @Override
    public Duration duration() {
        return duration;
    }
    //</editor-fold>

    //<editor-fold desc="Raw values for fields missing in Compilable">
    String rawPattern() {
        return pattern;
    }

    Object[] rawParameters() {
        return parameters;
    }

    Map<String, Object> rawFields() {
        return fields;
    }

    StackWalker.StackFrame[] rawStack() {
        return stack;
    }

    Throwable rawException() {
        return exception;
    }

    StackMode rawStackMode() {
        return stackMode;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void withLevel(Level level) {
        if (level != null) {
            this.level = level;
        }
    }

    public void withDepth(int depth) {
        if (depth >= 0) {
            this.depth = depth;
        }
    }

    public void withIcon(String icon) {
        this.icon = icon;
    }

    public void withName(String name) {
        this.name = name;
    }

    public void withPattern(String pattern) {
        this.pattern = pattern;
    }

    public void withParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void withField(String field, Object value) {
        if (field != null) {
            if (fields == null) {
                fields = Maps.newHashMap();
            }
            fields.put(field, value);
        }
    }

    public void withFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public void withDuration(Duration duration) {
        this.duration = duration;
    }

    public void withStack(StackWalker.StackFrame[] stack) {
        this.stack = stack;
    }

    public void withException(Throwable exception) {
        this.exception = exception;
    }

    public void withStackMode(StackMode stackMode) {
        if (stackMode != null) {
            this.stackMode = stackMode;
        }
    }
    //</editor-fold>
}

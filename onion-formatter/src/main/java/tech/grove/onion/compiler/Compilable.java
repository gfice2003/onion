package tech.grove.onion.compiler;

import tech.grove.onion.data.exception.ExceptionInfo;
import tech.grove.onion.data.stack.StackInfo;

import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;

public interface Compilable {

    String logger();

    Level level();

    int depth();

    String icon();

    Duration duration();

    String name();

    String message();

    Iterable<Map.Entry<String, Object>> fields();

    StackInfo stack();

    ExceptionInfo exception();
}

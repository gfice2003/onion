package tech.grove.onion.stubs;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import tech.grove.onion.backend.logger.BackendLogger;
import tech.grove.onion.compiler.Compiled;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class TestBackendLogger implements BackendLogger<String> {

    private final String                 name;
    private final List<Compiled<String>> written  = Lists.newArrayList();
    private final Set<Level>             disabled = Sets.newHashSet();

    public TestBackendLogger(String name) {
        this.name = name;
    }

    @Override
    public void write(Compiled<String> layer) {
        written.add(layer);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isEnabled(Level level) {
        return !disabled.contains(level);
    }

    public Iterable<Compiled<String>> written() {
        return written;
    }

    private void enable(Level level) {
        disabled.remove(level);
    }

    private void disable(Level level) {
        disabled.add(level);
    }

    public void reset() {
        written.clear();
        disabled.clear();
    }
}

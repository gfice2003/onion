package tech.grove.onion.data.preprocessors.duration;

import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.layer.LayerBuilder;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.builders.shell.ShellReportBuilder;
import tech.grove.onion.data.preprocessors.BasePreprocessor;
import tech.grove.onion.data.builders.BuilderConsumer;

import java.time.Duration;
import java.util.Stack;

public class DurationPreprocessor extends BasePreprocessor {

    private final Stack<Long> timestamps = new Stack<>();

    public DurationPreprocessor(BuilderConsumer nextConsumer) {
        super(nextConsumer);
    }

    private static long nowMillis() {
        return System.currentTimeMillis();
    }

    @Override
    protected ShellBuilder accept(ShellBuilder shell) {
        timestamps.add(nowMillis());
        return shell;
    }

    @Override
    protected BaseBuilder<?> accept(ShellReportBuilder shellReport) {
        var element  = timestamps.pop();
        var duration = Duration.ofMillis(nowMillis() - element);

        return shellReport.lastFor(duration);
    }

    @Override
    protected LayerBuilder<?> accept(LayerBuilder<?> layer) {
        return layer;
    }
}

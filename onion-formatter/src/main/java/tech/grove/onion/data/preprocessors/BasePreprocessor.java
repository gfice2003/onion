package tech.grove.onion.data.preprocessors;

import tech.grove.onion.data.builders.BuilderConsumer;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.layer.LayerBuilder;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.builders.shell.ShellReportBuilder;

public abstract class BasePreprocessor implements BuilderConsumer {

    private final BuilderConsumer next;

    protected BasePreprocessor(BuilderConsumer next) {
        this.next = next;
    }

    @Override
    public void accept(BaseBuilder<?> builder) {

        var result = switch (builder) {
            case ShellBuilder data -> accept(data);
            case ShellReportBuilder report -> accept(report);
            default -> accept((LayerBuilder<?>) builder);
        };

        if (result != null) {
            proceed(result);
        }
    }

    protected abstract BaseBuilder<?> accept(ShellBuilder shell);

    protected abstract BaseBuilder<?> accept(ShellReportBuilder shellReport);

    protected abstract BaseBuilder<?> accept(LayerBuilder<?> layer);

    protected void proceed(BaseBuilder<?> result) {
        next.accept(result);
    }
}

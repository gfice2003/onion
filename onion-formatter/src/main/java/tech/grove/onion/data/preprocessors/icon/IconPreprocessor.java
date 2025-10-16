package tech.grove.onion.data.preprocessors.icon;

import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.layer.LayerBuilder;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.builders.shell.ShellReportBuilder;
import tech.grove.onion.data.builders.BuilderConsumer;
import tech.grove.onion.data.preprocessors.BasePreprocessor;

public class IconPreprocessor extends BasePreprocessor {

    interface Icon {
        String SHELL        = "{+}";
        String SHELL_REPORT = "{-}";
    }

    public IconPreprocessor(BuilderConsumer nextConsumer) {
        super(nextConsumer);
    }

    @Override
    protected BaseBuilder<?> accept(ShellBuilder shell) {
        return shell.putIcon(Icon.SHELL);
    }

    @Override
    protected BaseBuilder<?> accept(ShellReportBuilder shellReport) {
        return shellReport.putIcon(Icon.SHELL_REPORT);
    }

    @Override
    protected BaseBuilder<?> accept(LayerBuilder<?> layer) {
        return layer;
    }
}

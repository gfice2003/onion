package tech.grove.onion.data.builders.shell;

import tech.grove.onion.api.shell.Report;
import tech.grove.onion.api.shell.Shell;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.preprocessors.icon.IconAware;

import java.util.function.Consumer;

public class ShellBuilder extends BaseBuilder<ShellBuilder> implements Shell, IconAware<ShellBuilder> {

    public ShellBuilder(BaseBuilder<?> source) {
        super(source);
    }

    ShellBuilder(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        super(consumer, data);
    }

    @Override
    public Report add(String pattern, Object... parameters) {

        data.withPattern(pattern);
        data.withParameters(parameters);

        commit();

        return new ShellReportBuilder(this);
    }

    @Override
    public ShellBuilder putIcon(String icon) {
        return setAndContinue(icon, data::withIcon, NullAction.SKIP);
    }
}

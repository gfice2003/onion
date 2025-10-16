package tech.grove.onion.data.builders.shell;

import tech.grove.onion.api.shell.Report;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.preprocessors.duration.Durable;
import tech.grove.onion.data.preprocessors.icon.IconAware;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ShellReportBuilder extends BaseBuilder<ShellReportBuilder> implements Report, IconAware<ShellReportBuilder>, Durable<ShellReportBuilder> {

    interface Token {
        String RESULT_FIELD = "Result";
    }

    private boolean skipped = false;

    public ShellReportBuilder(ShellBuilder shell) {
        this(shell.consumer(), dataFrom(shell.data()));
    }

    ShellReportBuilder(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        super(consumer, data);
    }

    private static DataCore dataFrom(DataCore shellData) {
        return new DataCore(shellData.logger()) {{
            withLevel(shellData.level());
        }};
    }

    public boolean isSkipped() {
        return skipped;
    }

    @Override
    public <R> R report(Level level, R result) {
        data.withLevel(level);
        data.withField(Token.RESULT_FIELD, result);

        return result;
    }

    @Override
    public void report(Level level, String pattern, Object... args) {
        data.withLevel(level);
        data.withPattern(pattern);
        data.withParameters(args);
    }

    @Override
    public ShellReportBuilder putIcon(String icon) {
        return setAndContinue(icon, data::withIcon, NullAction.SKIP);
    }

    @Override
    public ShellReportBuilder lastFor(Duration duration) {
        return setAndContinue(duration, data::withDuration, NullAction.SKIP);
    }

    @Override
    public void skip() {
        skipped = true;
    }

    @Override
    public void close() {
        commit();
    }
}

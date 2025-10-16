package tech.grove.onion.data.builders.layer;

import tech.grove.onion.api.layer.FieldsLayer;
import tech.grove.onion.data.DataCoreMerger;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.DataCore;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.builders.shell.ShellReportBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class FieldsLayerBuilder extends LayerBuilder<FieldsLayerBuilder> implements FieldsLayer, FieldsLayer.DefaultFieldsLayer {

    public FieldsLayerBuilder(Consumer<? super BaseBuilder<?>> consumer, DataCore data) {
        super(consumer, data);
    }

    public FieldsLayerBuilder(ShellBuilder shell, ShellReportBuilder report) {
        super(shell);

        DataCoreMerger
                .merge(shell.data())
                .with(report.data());
    }

    @Override
    public ValueSetter field(String name) {
        return value -> setAndContinue(checkName(name), x -> data.withField(x, value));
    }

    private String checkName(String name) {
        return Optional.ofNullable(name).orElse(Token.NO_NAME);
    }
}
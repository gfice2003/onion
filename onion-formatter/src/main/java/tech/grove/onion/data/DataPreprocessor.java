package tech.grove.onion.data;

import tech.grove.onion.data.builders.BuilderConsumer;
import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.preprocessors.depth.DepthPreprocessor;
import tech.grove.onion.data.preprocessors.duration.DurationPreprocessor;
import tech.grove.onion.data.preprocessors.icon.IconPreprocessor;
import tech.grove.onion.tools.chained.BaseChainedTransformer;

public class DataPreprocessor extends BaseChainedTransformer<BaseBuilder<?>, BaseBuilder<?>> implements DataPreprocessorApi {

    private final ThreadLocal<Conveyor> conveyors = new ThreadLocal<>();

    private Conveyor conveyor() {
        var result = conveyors.get();

        if (result == null) {
            conveyors.set(result = new Conveyor());
        }

        return result;
    }

    @Override
    protected BaseBuilder<?> transform(BaseBuilder<?> input) {
        conveyor().accept(input);
        return null;                 //-- Result will be returned to this.proceed by DepthPreprocessor
    }

    private final class Conveyor implements BuilderConsumer {

        private final IconPreprocessor     iconPreprocessor     = new IconPreprocessor(DataPreprocessor.this::proceed);
        private final DepthPreprocessor    depthPreprocessor    = new DepthPreprocessor(iconPreprocessor);
        private final DurationPreprocessor durationPreprocessor = new DurationPreprocessor(depthPreprocessor);

        @Override
        public void accept(BaseBuilder<?> builder) {
            entryPoint().accept(builder);
        }

        private BuilderConsumer entryPoint() {
            return durationPreprocessor;
        }
    }
}

package tech.grove.onion.data;

import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.tools.chained.ChainedTransformer;

public interface DataPreprocessorApi extends ChainedTransformer<BaseBuilder<?>, BaseBuilder<?>> {
}

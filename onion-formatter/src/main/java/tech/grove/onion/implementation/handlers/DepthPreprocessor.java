package tech.grove.onion.implementation.handlers;

import tech.grove.onion.data.layers.AbstractLayer;
import tech.grove.onion.data.layers.base.LayerBase;
import tech.grove.onion.data.layers.sandwich.SandwichIn;
import tech.grove.onion.data.layers.sandwich.SandwichOut;
import tech.grove.onion.exceptions.ArgumentNullException;
import tech.grove.onion.implementation.core.LayerConsumer;

import java.util.Optional;
import java.util.Stack;

public class DepthPreprocessor implements LayerConsumer {

    private final LayerConsumer  nextConsumer;
    private final Stack<Element> pending = new Stack<>();

    public DepthPreprocessor(LayerConsumer nextConsumer) {
        this.nextConsumer = Optional.ofNullable(nextConsumer)
                .orElseThrow(() -> new ArgumentNullException("nextConsumer"));
    }

    private int depth() {
        return pending.size();
    }

    @Override
    public void accept(LayerBase<? extends LayerBase<?>> layer) {
        if (layer instanceof SandwichIn in) {
            accept(in);
        } else if (layer instanceof SandwichOut out) {
            accept(out);
        } else {
            accept((AbstractLayer<?>) layer);
        }
    }

    private void accept(SandwichIn in) {
        pending.add(new Element(in));
    }

    private void accept(AbstractLayer<?> entry) {
        proceedPending();
        nextConsumer.accept(entry.diveTo(depth()));
    }

    private void accept(SandwichOut out) {
        var element = pending.pop();

        if (element.pending) {
            Optional.ofNullable(element.in.tryMerge(out))
                    .ifPresent(nextConsumer::accept);
        } else {
            nextConsumer.accept(out.diveTo(element.in.depth()));
        }
    }

    private boolean pendingProceeded() {
        return pending.isEmpty() || !pending.getFirst().pending;
    }

    private void proceedPending() {

        if (pendingProceeded()) {
            return;
        }

        for (Element element : pending) {
            element.proceed();
        }
    }

    private final class Element {

        private final SandwichIn in;
        private       boolean    pending = true;

        private Element(SandwichIn in) {
            this.in = in.diveTo(depth());
        }

        public void proceed() {
            nextConsumer.accept(in);
            pending = false;
        }
    }
}

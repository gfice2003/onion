package tech.grove.onion.data.preprocessors.depth;

import tech.grove.onion.data.builders.base.BaseBuilder;
import tech.grove.onion.data.builders.layer.FieldsLayerBuilder;
import tech.grove.onion.data.builders.layer.LayerBuilder;
import tech.grove.onion.data.builders.shell.ShellBuilder;
import tech.grove.onion.data.builders.shell.ShellReportBuilder;
import tech.grove.onion.data.builders.BuilderConsumer;
import tech.grove.onion.data.preprocessors.BasePreprocessor;

import java.util.Stack;

public class DepthPreprocessor extends BasePreprocessor {

    private final Stack<Element> pending = new Stack<>();

    public DepthPreprocessor(BuilderConsumer nextConsumer) {
        super(nextConsumer);
    }

    private int depth() {
        return pending.size();
    }

    @Override
    protected ShellBuilder accept(ShellBuilder shell) {
        pending.add(new Element(shell));
        return null;
    }

    @Override
    protected BaseBuilder<?> accept(ShellReportBuilder shellReport) {
        var element = pending.pop();

        if (element.pending) {
            if (shellReport.isSkipped()) {
                return null;
            }
            return new FieldsLayerBuilder(element.shell, shellReport);
        } else {
            return shellReport.diveTo(element.depth());
        }
    }

    @Override
    protected LayerBuilder<?> accept(LayerBuilder<?> layer) {
        proceedPending();
        return layer.diveTo(depth());
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

        private final ShellBuilder shell;
        private final int          depth;

        private boolean pending = true;

        private Element(ShellBuilder shell) {
            this.depth = depth();
            this.shell = shell.diveTo(depth);
        }

        public int depth() {
            return depth;
        }

        public void proceed() {
            DepthPreprocessor.this.proceed(shell);
            pending = false;
        }
    }
}

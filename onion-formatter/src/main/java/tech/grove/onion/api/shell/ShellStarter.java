package tech.grove.onion.api.shell;

import tech.grove.onion.tools.stack.StackHelper;

public interface ShellStarter {

    StackHelper STACK = new StackHelper().withKnown(ShellStarter.class);

    default Adder methodShell() {
        return shell(STACK.getFirstUnknownFrame().getMethodName());
    }

    Adder shell(String name);
}
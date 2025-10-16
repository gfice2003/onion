package tech.grove.onion.api.layer;

import tech.grove.onion.api.common.Exception;
import tech.grove.onion.api.shell.ShellStarter;

import java.util.logging.Level;

public interface Layer extends Exception<DefaultAdder, LayerExceptionStack>, LayerStack, ShellStarter, Adder {

    Level level();

    static void main(String[] args) {
        Layer l = null;

        l.add("Hello");

        l.putStack().add();
        l.putStack().add("Hello");

        l.exception(new IllegalArgumentException()).add();
        l.exception(new IllegalArgumentException()).add("Hello");
        l.exception(new IllegalArgumentException()).fullStack().add();
        l.exception(new IllegalArgumentException()).fullStack().add("Hello");

        try(var ignored = l.methodShell().add()){
        }

        try(var ignored = l.methodShell().add("Hello")){
        }

        try(var ignored = l.shell("shell").add()){
        }

        try(var ignored = l.shell("shell").add("Hello")){
        }

        try(var shell = l.methodShell().add()){
            shell.report(new Object());
        }

        try(var shell = l.methodShell().add()){
            shell.report("Hello");
        }
    }
}

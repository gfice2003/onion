package tech.grove.onion.api.shell;

public interface Shell extends Adder {

    static void main(String[] args) {

        Shell shell = null;

        try(var s = shell.add()){
            s.report(new Object());
        }

        try(var s = shell.add()){
            s.report("Hello");
        }

        try(var s = shell.add("Hello")){
            s.report(new Object());
        }

        try(var s = shell.add("Hello")){
            s.report("Hello");
        }
    }
}

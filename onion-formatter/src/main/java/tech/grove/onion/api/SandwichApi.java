package tech.grove.onion.api;

import tech.grove.onion.tools.StackHelper;

import java.util.logging.Level;

public interface SandwichApi {

    StackHelper STACK = new StackHelper().withKnown(NameSetter.class);

    interface Token {
        String RESULT_PATTERN = "%s";
    }

    interface NameSetter {

        default Adder methodLayer() {
            return layer(STACK.getFirstUnknownFrame().getMethodName());
        }

        Adder layer(String name);
    }

    interface Adder {

        default Reporter add() {
            return add(null);
        }

        Reporter add(String pattern, Object... parameters);
    }

    interface Reporter extends AutoCloseable {

        default <R> R report(R result) {
            return report(null, result);
        }

        default <R> R reportFinest(R result) {
            return report(Level.FINEST, result);
        }

        default <R> R reportFiner(R result) {
            return report(Level.FINER, result);
        }

        default <R> R reportFine(R result) {
            return report(Level.FINE, result);
        }

        default <R> R reportConfig(R result) {
            return report(Level.CONFIG, result);
        }

        default <R> R reportInfo(R result) {
            return report(Level.INFO, result);
        }

        default <R> R reportWarning(R result) {
            return report(Level.WARNING, result);
        }

        default <R> R reportSevere(R result) {
            return report(Level.SEVERE, result);
        }

        default <R> R report(Level level, R result) {
            report(level, Token.RESULT_PATTERN, result);
            return result;
        }

        default void report(String pattern, Object... args) {
            report(null, pattern, args);
        }

        default void reportFinest(String pattern, Object... args) {
            report(Level.FINEST, pattern, args);
        }

        default void reportFiner(String pattern, Object... args) {
            report(Level.FINER, pattern, args);
        }

        default void reportFine(String pattern, Object... args) {
            report(Level.FINE, pattern, args);
        }

        default void reportConfig(String pattern, Object... args) {
            report(Level.CONFIG, pattern, args);
        }

        default void reportInfo(String pattern, Object... args) {
            report(Level.INFO, pattern, args);
        }

        default void reportWarning(String pattern, Object... args) {
            report(Level.WARNING, pattern, args);
        }

        default void reportSevere(String pattern, Object... args) {
            report(Level.SEVERE, pattern, args);
        }

        void report(Level level, String pattern, Object... args);

        void skip();

        @Override
        void close();
    }
}

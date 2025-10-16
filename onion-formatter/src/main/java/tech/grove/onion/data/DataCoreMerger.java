package tech.grove.onion.data;

import org.apache.commons.lang3.ArrayUtils;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Level;

public class DataCoreMerger {

    public static AnotherSetter merge(DataCore a) {
        return b -> merge(a, b);
    }

    public interface AnotherSetter {
        DataCore with(DataCore b);
    }

    private static DataCore merge(DataCore a, DataCore b) {
        return tryMerge(a, b, DataCoreMerger::doMerge);
    }

    private static DataCore doMerge(DataCore a, DataCore b) {

        a.withLevel(tryMerge(a.level(), b.level(), DataCoreMerger::doMergeLevel));
        a.withDepth(tryMerge(a.depth(), b.depth(), DataCoreMerger::doMergeDepth));
        a.withIcon(tryMerge(a.icon(), b.icon(), DataCoreMerger::returnFirst));
        a.withName(tryMerge(a.name(), b.name(), DataCoreMerger::returnFirst));
        a.withPattern(tryMerge(a.rawPattern(), b.rawPattern(), DataCoreMerger::doMergePattern));
        a.withParameters(tryMerge(a.rawParameters(), b.rawParameters(), DataCoreMerger::doMergeParameters));
        a.withFields(tryMerge(a.rawFields(), b.rawFields(), DataCoreMerger::doMergeFields));
        a.withDuration(tryMerge(a.duration(), b.duration(), DataCoreMerger::doMergeDuration));
        a.withStack(tryMerge(a.rawStack(), b.rawStack(), DataCoreMerger::returnFirst));
        a.withException(tryMerge(a.rawException(), b.rawException(), DataCoreMerger::returnFirst));
        a.withStackMode(tryMerge(a.rawStackMode(), b.rawStackMode(), DataCoreMerger::returnFirst));

        return a;
    }

    private static <T> T returnFirst(T a, T b) {
        return a;
    }

    private static Level doMergeLevel(Level a, Level b) {
        return a.intValue() > b.intValue() ? a : b;
    }

    private static int doMergeDepth(int a, int b) {
        return Math.max(a, b);
    }

    private static String doMergePattern(String a, String b) {
        return a + b;
    }

    private static Duration doMergeDuration(Duration a, Duration b) {
        if (a.compareTo(Duration.ZERO) != 0) {
            return a;
        } else {
            return b;
        }
    }

    private static Map<String, Object> doMergeFields(Map<String, Object> a, Map<String, Object> b) {
        a.putAll(b);
        return a;
    }

    private static Object[] doMergeParameters(Object[] a, Object[] b) {
        return ArrayUtils.addAll(a, b);
    }

    private static <T> T tryMerge(T a, T b, BiFunction<T, T, T> doMerge) {
        if (a != null && b != null) {
            return doMerge.apply(a, b);
        } else if (a != null) {
            return a;
        } else {
            return b;
        }
    }
}

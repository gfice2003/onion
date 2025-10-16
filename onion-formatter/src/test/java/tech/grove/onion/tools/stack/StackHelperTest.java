package tech.grove.onion.tools.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.grove.onion.data.stack.StackInfo;
import tech.grove.onion.data.stack.StackMode;

import static org.junit.jupiter.api.Assertions.*;
import static tech.grove.onion.utils.StackUtils.*;

public class StackHelperTest {

    @Nested
    class TopStackFrameResolution {

        @BeforeEach
        public void initializeTest() {
            target = new StackHelper();
        }

        private StackHelper            target;
        private StackWalker.StackFrame expectedFrame;
        private StackWalker.StackFrame resultFrame;

        @Test
        public void getFirstUnknown_default_returnsCurrentMethod() {
            given:
            {
                expectedFrame = frameFor(TopStackFrameResolution.class, "getFirstUnknown_default_returnsCurrentMethod");
            }
            when:
            {
                resultFrame = target.getFirstUnknownFrame();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        public void getFirstUnknown_shallow_returnsExpectedMethod() {
            given:
            {
                expectedFrame = frameFor(ShallowWrap.class, "get");
            }
            when:
            {
                resultFrame = new ShallowWrap().getShallow();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        public void getFirstUnknown_shallowWithKnownShallowWrap_returnsCurrentMethod() {
            given:
            {
                target.withKnown(ShallowWrap.class);
                expectedFrame = frameFor(TopStackFrameResolution.class, "getFirstUnknown_shallowWithKnownShallowWrap_returnsCurrentMethod");
            }
            when:
            {
                resultFrame = new ShallowWrap().getShallow();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        public void getFirstUnknown_deep_returnsExpectedMethod() {
            given:
            {
                expectedFrame = frameFor(ShallowWrap.class, "get");
            }
            when:
            {
                resultFrame = new ShallowWrap().getDeep();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        public void getFirstUnknown_deepWithKnownShallowWrap_returnsDeepWrapMethod() {
            given:
            {
                target.withKnown(ShallowWrap.class);
                expectedFrame = frameFor(ShallowWrap.DeepWrap.class, "getElement");
            }
            when:
            {
                resultFrame = new ShallowWrap().getDeep();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        @Test
        public void getFirstUnknown_deepWithKnownShallowWrapAndDeepWrap_returnsCurrentMethod() {
            given:
            {
                target.withKnown(ShallowWrap.class).withKnown(ShallowWrap.DeepWrap.class);
                expectedFrame = frameFor(TopStackFrameResolution.class, "getFirstUnknown_deepWithKnownShallowWrapAndDeepWrap_returnsCurrentMethod");
            }
            when:
            {
                resultFrame = new ShallowWrap().getDeep();
            }
            then:
            {
                validate(expectedFrame, resultFrame);
            }
        }

        private final class ShallowWrap {

            private final DeepWrap deep = new DeepWrap();

            StackWalker.StackFrame getShallow() {
                return get();
            }

            private StackWalker.StackFrame get() {
                return target.getFirstUnknownFrame();
            }

            StackWalker.StackFrame getDeep() {
                return deep.getElement();
            }

            private final class DeepWrap {
                StackWalker.StackFrame getElement() {
                    return get();
                }
            }
        }
    }

    @Nested
    class TrimStackFunctionality {

        private StackWalker.StackFrame[] expectedStack;
        private StackWalker.StackFrame[] sourceStack;
        private StackWalker.StackFrame[] resultStack;

        @Test
        public void trim_null_returnsEmptyStack() {
            given:
            {
                sourceStack   = null;
                expectedStack = stackOfSize(0);
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        public void trim_notEmptyStackWithNONE_returnsEmptyStack() {
            given:
            {
                sourceStack   = stackOfSize(3);
                expectedStack = stackOfSize(0);
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.NONE)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        public void trim_stackLessThenDefaultLimitWithFAIR_returnsSourceStack() {
            given:
            {
                sourceStack   = stackOfSize(StackHelper.Default.LIMIT - 1);
                expectedStack = sourceStack;
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        public void trim_stackGreaterThenDefaultLimitWithFAIR_returnsTrimmedStack() {
            given:
            {
                sourceStack   = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedStack = stackOfSize(StackHelper.Default.LIMIT);
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FAIR)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }

        @Test
        public void trim_stackGreaterThenDefaultLimitWithFULL_returnsSourceStack() {
            given:
            {
                sourceStack   = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedStack = sourceStack;
            }
            when:
            {
                resultStack = StackHelper
                        .trim(sourceStack)
                        .mode(StackMode.FULL)
                        .toArray(StackWalker.StackFrame[]::new);
            }
            then:
            {
                validate(expectedStack, resultStack);
            }
        }
    }

    @Nested
    class ConvertStackToInfo {

        private StackWalker.StackFrame[] sourceStack;
        private StackInfo                expectedIfo;
        private StackInfo                resultIfo;

        @Test
        public void toInfo_null_returnsEmptyStack() {
            when:
            {
                resultIfo = StackHelper
                        .toInfo((StackWalker.StackFrame[]) null)
                        .mode(StackMode.FAIR);
            }
            then:
            {
                assertNull(resultIfo);
            }
        }

        @Test
        public void toInfo_notEmptyStackWithNULL_returnsNull() {
            given:
            {
                sourceStack = stackOfSize(3);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(null);
            }
            then:
            {
                assertNull(resultIfo);
            }
        }

        @Test
        public void toInfo_notEmptyStackWithNONE_returnsEmptyStack() {
            given:
            {
                sourceStack = stackOfSize(3);
                expectedIfo = new StackInfo(StackMode.NONE, stackOfSize(0), 3);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.NONE);
            }
            then:
            {
                assertEquals(expectedIfo, resultIfo);
            }
        }

        @Test
        public void toInfo_stackLessThenDefaultLimitWithFAIR_returnsSourceStack() {
            given:
            {
                sourceStack = stackOfSize(StackHelper.Default.LIMIT - 1);
                expectedIfo = new StackInfo(StackMode.FAIR, sourceStack, sourceStack.length);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.FAIR);
            }
            then:
            {
                assertEquals(expectedIfo, resultIfo);
            }
        }

        @Test
        public void toInfo_stackGreaterThenDefaultLimitWithFAIR_returnsTrimmedStack() {
            given:
            {
                sourceStack = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedIfo = new StackInfo(StackMode.FAIR, stackOfSize(StackHelper.Default.LIMIT), StackHelper.Default.LIMIT + 1);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.FAIR);
            }
            then:
            {
                assertEquals(expectedIfo, resultIfo);
            }
        }

        @Test
        public void toInfo_stackGreaterThenDefaultLimitWithFULL_returnsSourceStack() {
            given:
            {
                sourceStack = stackOfSize(StackHelper.Default.LIMIT + 1);
                expectedIfo = new StackInfo(StackMode.FULL, sourceStack, sourceStack.length);
            }
            when:
            {
                resultIfo = StackHelper
                        .toInfo(sourceStack)
                        .mode(StackMode.FULL);
            }
            then:
            {
                assertEquals(expectedIfo, resultIfo);
            }
        }
    }

    private void validate(StackWalker.StackFrame[] expected, StackWalker.StackFrame[] result) {

        if (expected == result) {
            return;
        }

        if (expected != null && result != null) {

            if (expected.length != result.length) {
                fail("Invalid result length, expected %s but got %s".formatted(expected.length, result.length));
            }

            for (var i = 0; i < expected.length; i++) {
                if (!expected[i].equals(result[i])) {
                    fail("Invalid frame at position %s, expected %s but got %s".formatted(i, expected[i], result[i]));
                }
            }
        } else if (expected == null) {
            fail("Expected null but got %s elements".formatted(result.length));
        } else {
            fail("Expected %s elements but got null".formatted(expected.length));
        }
    }

    private void validate(StackWalker.StackFrame expected, StackWalker.StackFrame result) {
        assertEquals(expected.getClassName(), result.getClassName());
        assertEquals(expected.getMethodName(), result.getMethodName());
    }
}

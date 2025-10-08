package tech.grove.onion.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.grove.onion.exceptions.ArgumentNullException;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {

    interface Data {
        String INITIAL = "initial";
        String CHANGED = "changed";
    }

    private TestBuilder target;

    @BeforeEach
    public void initializeTest() {
        target = new TestBuilder(Data.INITIAL);
    }

    @Test
    public void acceptNull_validValue_changesValue() {
        when:
        {
            target.acceptNull(Data.CHANGED);
        }
        then:
        {
            assertEquals(Data.CHANGED, target.value());
        }
    }

    @Test
    public void acceptNull_null_changesValue() {
        when:
        {
            target.acceptNull(null);
        }
        then:
        {
            assertNull(target.value());
        }
    }

    @Test
    public void skipNull_validValue_changesValue() {
        when:
        {
            target.skipNull(Data.CHANGED);
        }
        then:
        {
            assertEquals(Data.CHANGED, target.value());
        }
    }

    @Test
    public void skipNull_null_leavesValueUnchanged() {
        when:
        {
            target.skipNull(null);
        }
        then:
        {
            assertEquals(Data.INITIAL, target.value());
        }
    }

    @Test
    public void throwOnNull_validValue_changesValue() {
        when:
        {
            target.throwOnNull(Data.CHANGED);
        }
        then:
        {
            assertEquals(Data.CHANGED, target.value());
        }
    }

    @Test
    public void throwOnNull_null_throwsArgumentNullException() {
        then:
        {
            assertThrows(ArgumentNullException.class, () -> target.throwOnNull(null));
        }
    }

    private static final class TestBuilder extends Builder<TestBuilder> {

        private String value;

        public TestBuilder(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public void acceptNull(String value) {
            setAndContinue(value, x -> this.value = x, NullAction.ACCEPT);
        }

        public void skipNull(String value) {
            setAndContinue(value, x -> this.value = x, NullAction.SKIP);
        }

        public void throwOnNull(String value) {
            setAndContinue(value, x -> this.value = x, NullAction.THROW);
        }
    }
}

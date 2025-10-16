package tech.grove.onion.log.context.sampler;

import org.junit.jupiter.api.Test;
import tech.grove.onion.data.context.Handle;
import tech.grove.onion.tools.stack.StackHelper;
import tech.grove.onion.stubs.TestHandle;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.grove.onion.log.context.sampler.LogEntrySamplerTest.Data.*;

public class LogEntrySamplerTest {

    private static final StackHelper STACK = new StackHelper();

    interface Data {
        Handle   HANDLE_1 = new TestHandle("class_1", "method_1", 1);
        Handle   HANDLE_2 = new TestHandle("class_2", "method_2", 2);
        int      HITS     = 10;
        Duration INTERVAL = Duration.ofSeconds(10);
    }

    private LogEntrySampler target;

    @Test
    public void checkHits_forSameHandler_worksAsExpected() {
        given:
        {
            target = new LogEntrySampler();
        }
        then:
        {
            assertTrue(target.check(HANDLE_1, HITS));
            assertFalse(target.check(HANDLE_1, HITS));
        }
    }

    @Test
    public void checkHits_forDifferentHandler_worksIndependently() {
        given:
        {
            target = new LogEntrySampler();
        }
        then:
        {
            assertTrue(target.check(HANDLE_1, HITS));
            assertTrue(target.check(HANDLE_2, HITS));
        }
    }

    @Test
    public void checkInterval_forSameHandler_worksAsExpected() {
        given:
        {
            target = new LogEntrySampler();
        }
        then:
        {
            assertTrue(target.check(HANDLE_1, INTERVAL));
            assertFalse(target.check(HANDLE_1, INTERVAL));
        }
    }

    @Test
    public void checkInterval_forDifferentHandler_worksIndependently() {
        given:
        {
            target = new LogEntrySampler();
        }
        then:
        {
            assertTrue(target.check(HANDLE_1, INTERVAL));
            assertTrue(target.check(HANDLE_2, INTERVAL));
        }
    }
}

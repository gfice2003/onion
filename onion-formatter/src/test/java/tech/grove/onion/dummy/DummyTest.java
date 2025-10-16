package tech.grove.onion.dummy;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DummyTest {

    private TestInterface target;

    @Test
    public void proxyFor_interface_returnsValidInstance() {
        then:
        {
            assertNotNull(Dummy.proxyFor(TestInterface.class));
        }
    }

    @Test
    public void child_default_returnsValidInstance() {
        given:
        {
            target = Dummy.proxyFor(TestInterface.class);
        }
        then:
        {
            assertNotNull(target.child());
        }
    }

    @Test
    public void proxyIntProperty_default_returnsDefaultIntValue() {
        given:
        {
            target = Dummy.proxyFor(TestInterface.class);
        }
        then:
        {
            assertEquals(0, target.intProperty());
        }
    }

    @Test
    public void proxyObjectProperty_default_returnsNull() {
        given:
        {
            target = Dummy.proxyFor(TestInterface.class);
        }
        then:
        {
            assertNull(target.objectProperty());
        }
    }

    @Test
    public void childBooleanProperty_default_returnsDefaultBooleanValue() {
        given:
        {
            target = Dummy.proxyFor(TestInterface.class);
        }
        then:
        {
            assertFalse(target.child().booleanProperty());
        }
    }

    @Test
    public void chilfLocalDateTimeProperty_default_returnsNull() {
        given:
        {
            target = Dummy.proxyFor(TestInterface.class);
        }
        then:
        {
            assertNull(target.child().localDateTimeProperty());
        }
    }


    interface TestInterface {

        TestChildInterface child();

        int intProperty();

        Object objectProperty();
    }

    interface TestChildInterface {

        boolean booleanProperty();

        LocalDateTime localDateTimeProperty();
    }
}

package com.bericotech.dropwizard.nagios;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * @author Raj.Patel - https://code.google.com/p/jsendnsca/
 *         Richard Clayton (Berico Technologies)
 */
public class LevelTest {

    @SuppressWarnings({"PublicField"})
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnIntegerValueForLevel() throws Exception {
        assertEquals(0L, (long) Level.OK.ordinal());
    }

    @Test
    public void shouldReturnLevelForLevelIntegerValue() throws Exception {
        assertEquals(Level.OK, Level.toLevel(0));
        assertEquals(Level.WARNING, Level.toLevel(1));
        assertEquals(Level.CRITICAL, Level.toLevel(2));
        assertEquals(Level.UNKNOWN, Level.toLevel(3));
    }

    @Test
    public void shouldThrowIllegalAgrumentExceptionForInvalidLevelValue() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("LevelValue [4] is not a valid level");

        Level.toLevel(4);
    }

    @Test
    public void shouldReturnCorrectLevelForStringVersionIgnoringCaseAndWhitespace() throws Exception {
        assertEquals(Level.OK, Level.tolevel("ok"));
        assertEquals(Level.WARNING, Level.tolevel("warning"));
        assertEquals(Level.CRITICAL, Level.tolevel("critical"));
        assertEquals(Level.UNKNOWN, Level.tolevel("unknown"));

        assertEquals(Level.OK, Level.tolevel("OK"));
        assertEquals(Level.WARNING, Level.tolevel("WARNING"));
        assertEquals(Level.CRITICAL, Level.tolevel("CRITICAL"));
        assertEquals(Level.UNKNOWN, Level.tolevel("UNKNOWN"));

        assertEquals(Level.OK, Level.tolevel("ok "));
        assertEquals(Level.WARNING, Level.tolevel("WarNinG"));
    }

    @Test
    public void should_return_OK_if_under_warning_criteria(){

        Level shouldBeOk = Level.evaluate(10l, 11l, 12l);

        assertEquals(Level.OK, shouldBeOk);
    }

    @Test
    public void should_return_WARNING_if_between_warning_and_critical(){

        Level shouldBeWarn = Level.evaluate(15l, 10l, 20l);

        assertEquals(Level.WARNING, shouldBeWarn);
    }

    @Test
    public void should_return_CRITICAL_if_over_critical_criteria(){

        Level shouldBeCritical = Level.evaluate(12l, 10l, 11l);

        assertEquals(Level.CRITICAL, shouldBeCritical);
    }
}
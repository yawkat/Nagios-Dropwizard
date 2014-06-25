package com.bericotech.dropwizard.nagios;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class NagiosCheckTaskTest {

    @Test
    public void execute_handles_successful_check() throws Exception {

        final String expectedMessage = "expected message.";
        final Level expectedLevel = Level.OK;

        NagiosCheckTask task = new NagiosCheckTask("test") {

            @Override
            public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

                return new MessagePayloadBuilder().withLevel(expectedLevel).withMessage(expectedMessage).build();
            }
        };

        ImmutableMultimap<String, String> params = ImmutableMultimap.of();

        PrintWriter pw = mock(PrintWriter.class);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        task.execute(params, pw);

        verify(pw).println(payload.capture());

        assertEquals(expectedLevel + " - " + expectedMessage, payload.getValue());
    }

    @Test
    public void execute_handles_failed_check() throws Exception {

        final Exception expectedException = new Exception("expected error");

        NagiosCheckTask task = new NagiosCheckTask("test") {

            @Override
            public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

                throw expectedException;
            }
        };

        ImmutableMultimap<String, String> params = ImmutableMultimap.of();

        PrintWriter pw = mock(PrintWriter.class);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);

        task.execute(params, pw);

        verify(pw).println(payload.capture());

        assertEquals(
                NagiosCheckTask.DEFAULT_LEVEL_FOR_TASK_ERROR + " - " + expectedException.getMessage(),
                payload.getValue());
    }

    @Test
    public void getParameter_returns_absent_value_if_key_doesnt_exist(){

        ImmutableMultimap<String, String> params = ImmutableMultimap.of("key1", "value1");

        Optional<String> shouldBeAbsent = NagiosCheckTask.getParameter(params, "key2");

        assertFalse(shouldBeAbsent.isPresent());
    }


    @Test
    public void getParameter_returns_present_value_if_key_does_exist(){

        String expectedValue = "value1";

        ImmutableMultimap<String, String> params = ImmutableMultimap.of("key1", expectedValue);

        Optional<String> shouldBePresent = NagiosCheckTask.getParameter(params, "key1");

        assertTrue(shouldBePresent.isPresent());

        assertEquals(expectedValue, shouldBePresent.get());
    }

    @Test
    public void getMandatoryParameter_returns_value_if_present() throws UnsatisfiedParameterException {

        String expectedValue = "value1";

        ImmutableMultimap<String, String> params = ImmutableMultimap.of("key1", expectedValue);

        String actualValue = NagiosCheckTask.getMandatoryParameter(params, "key1");

        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = UnsatisfiedParameterException.class)
    public void getMandatoryParameter_throws_exception_is_key_is_not_present() throws UnsatisfiedParameterException {

        ImmutableMultimap<String, String> params = ImmutableMultimap.of("key1", "value2");

        NagiosCheckTask.getMandatoryParameter(params, "key2");

        fail("Exception should have been thrown.");
    }
}

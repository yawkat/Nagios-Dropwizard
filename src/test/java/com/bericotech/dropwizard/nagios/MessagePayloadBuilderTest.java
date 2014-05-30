package com.bericotech.dropwizard.nagios;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Raj.Patel - https://code.google.com/p/jsendnsca/
 *         Richard Clayton (Berico Technologies)
 */
public class MessagePayloadBuilderTest {

    @Test
    public void shouldConstructNewMessagePayload() throws Exception {
        final MessagePayload messagePayload = new MessagePayloadBuilder()
                .withLevel(Level.CRITICAL)
                .withMessage("test message")
                .create();

        assertEquals(Level.CRITICAL, messagePayload.getLevel());
        assertEquals("test message", messagePayload.getMessage());
    }

    @Test
    public void shouldConstructNewMessagePayloadWithPerfData() throws Exception {
        final MessagePayload messagePayload = new MessagePayloadBuilder()
                .withLevel(Level.CRITICAL)
                .withMessage("test message")
                .withPerfData(PerfDatum.builder("queue_count", 12).build())
                .withPerfData(PerfDatum.builder("queue_capacity", 32).criteria(50, 100).minMax(1, 45).build())
                .create();

        assertEquals(Level.CRITICAL, messagePayload.getLevel());
        assertEquals("test message | queue_count=12 | queue_capacity=32;50;100;1;45", messagePayload.getMessage());
    }


    @Test
    public void shouldConstructTwoNewMessagePayload() throws Exception {
        final MessagePayload messagePayload = new MessagePayloadBuilder()
                .withLevel(Level.OK)
                .withMessage("test message")
                .create();

        final MessagePayload messagePayload2 = new MessagePayloadBuilder()
                .withLevel(Level.WARNING)
                .withMessage("foo message")
                .create();

        assertEquals(Level.OK, messagePayload.getLevel());
        assertEquals("test message", messagePayload.getMessage());

        assertEquals(Level.WARNING, messagePayload2.getLevel());
        assertEquals("foo message", messagePayload2.getMessage());
    }

}
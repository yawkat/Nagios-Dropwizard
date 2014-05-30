/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bericotech.dropwizard.nagios;

import java.util.Collection;

/**
 * Used to construct a {@link MessagePayload} using a builder pattern e.g.
 *
 * <pre>
 * MessagePayload payload = new MessagePayloadBuilder()
 *      .withHostname(&quot;localhost&quot;)
 *      .withLevel(Level.CRITICAL)
 *      .withServiceName(&quot;Test Service Name&quot;)
 *      .withMessage(&quot;Test Message&quot;)
 *      .create();
 * </pre>
 *
 * @author Raj.Patel - https://code.google.com/p/jsendnsca/
 *         Richard Clayton (Berico Technologies)
 * @since 1.2
 */
public class MessagePayloadBuilder {

    private final MessagePayload payload = new MessagePayload();

    /**
     * Return the built {@link MessagePayload}
     *
     * @return the built {@link MessagePayload}
     */
    public MessagePayload create() {
        return payload;
    }

    /**
     * Set the level of the passive check
     *
     * @param level
     *            the level value
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withLevel(int level) {
        payload.setLevel(Level.toLevel(level));
        return this;
    }

    /**
     * Set the level of the passive check
     *
     * @param level
     *            the {@link Level}
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withLevel(Level level) {
        payload.setLevel(level);
        return this;
    }

    /**
     * Set the message of the passive check
     *
     * @param message
     *            the message
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withMessage(String message) {
        payload.setMessage(message);
        return this;
    }

    /**
     * Set the performance data associated with this Message.
     * This may be called multiple times, adding new performance data
     * to the internal list.
     * @param perfData
     *            the performance data
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withPerfData(PerfDatum... perfData){

        payload.addPerfData(perfData);
        return this;
    }

    /**
     * Set the performance data associated with this Message.
     * This may be called multiple times, adding new performance data
     * to the internal list.
     * @param perfData
     *            the performance data
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withPerfData(Collection<PerfDatum> perfData){

        payload.addPerfData(perfData);
        return this;
    }
}
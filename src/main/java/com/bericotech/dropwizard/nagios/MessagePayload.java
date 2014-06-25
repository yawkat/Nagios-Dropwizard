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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * The Passive Check Message Payload
 *
 * @author Raj.Patel - https://code.google.com/p/jsendnsca/
 *         Richard Clayton (Berico Technologies)
 * @version 1.0
 */
public class MessagePayload {

    private Level level = Level.UNKNOWN;
    private String message = StringUtils.EMPTY;
    private List<PerfDatum> perfData = new ArrayList<PerfDatum>();

    /**
     * Used by the Builder.
     */
    MessagePayload(){}

    /**
     * Construct a new {@link MessagePayload}
     *
     * @param level
     *            the level
     * @param message
     *            the message
     */
    public MessagePayload(Level level, String message) {

        this.level = level;
        this.message = message;
    }

    /**
     * Construct a new {@link MessagePayload}
     *
     * @param level
     *            the level
     * @param message
     *            the message
     * @param perfData
     *            An array of performance data related to the check.
     */
    public MessagePayload(Level level, String message, PerfDatum... perfData) {

        this.level = level;
        this.message = message;
        this.perfData.addAll(Arrays.asList(perfData));
    }

    /**
     * Get the level of the Passive check
     *
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Set the level of the Passive check using a {@link String} The case of the
     * {@link String} is ignored
     *
     * @param level
     *            either "ok", "warning", "critical" or "unknown"
     */
    public void setLevel(String level) {
        this.level = Level.tolevel(level);
    }

    /**
     * Set the level of the Passive check
     *
     * @param level
     *            the level
     */
    public void setLevel(Level level) {
        this.level = level;

    }

    /**
     * Performance Data Related to the check.
     * @return List of Performance data.
     */
    public List<PerfDatum> getPerfData(){

        return this.perfData;
    }

    /**
     * Set the list of performance data.
     * @param perfData
     */
    public void setPerfData(List<PerfDatum> perfData){

        this.perfData.clear();
        this.perfData.addAll(perfData);
    }

    /**
     * Add PerfData to the Message
     * @param perfData Performance Data
     */
    public void addPerfData(PerfDatum... perfData){

        this.perfData.addAll(Arrays.asList(perfData));
    }

    /**
     * Add PerfData to the Message
     * @param perfData Performance Data
     */
    public void addPerfData(Collection<PerfDatum> perfData){

        this.perfData.addAll(perfData);
    }

    /**
     * Get the Base Message (without PerfData).
     * @return Base Message.
     */
    public String getBaseMessage(){

        return this.message;
    }

    /**
     * The message to send in this passive check
     *
     * @return the message, default is an empty string
     */
    public String getMessage() {

        StringBuilder sb = new StringBuilder();

        sb.append(message);

        for (PerfDatum datum : perfData) {

            sb.append(" | ").append(datum.toString());
        }

        return sb.toString();
    }

    /**
     * Set the message to send in this passive check
     *
     * @param message
     *            the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 57)
                .append(level)
                .append(message)
                .toHashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MessagePayload == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MessagePayload other = (MessagePayload) obj;

        return new EqualsBuilder()
                .append(level, other.level)
                .append(message, other.message)
                .isEquals();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("level", level)
                .append("message", message)
                .append("perfData", perfData)
                .toString();
    }

    /**
     * Provides and instance of a builder used to construct this object.
     * @return Builder instance.
     */
    public static MessagePayloadBuilder builder() {

        return new MessagePayloadBuilder();
    }
}
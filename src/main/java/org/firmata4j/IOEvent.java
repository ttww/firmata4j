/* 
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Oleg Kurbatov (o.v.kurbatov@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.firmata4j;

import java.util.Map;

import org.firmata4j.firmata.FirmataDevice;

/**
 * An event which indicates that the state of an {@link IODevice} changed.
 *
 * @author @author Oleg Kurbatov &lt;o.v.kurbatov@gmail.com&gt;
 */
public class IOEvent {

    private final IODevice device;
    private final Pin pin;
    private final long value;
    private final long timestamp;
    private final Map<String, Object> map;

    /**
     * Constructs the event is relevant to {@link IODevice} as a whole.
     *
     * @param device the device that originated the event
     */
    public IOEvent(IODevice device) {
        this(device, System.currentTimeMillis());
    }

    /**
     * Constructs the event is relevant to {@link IODevice} as a whole.
     *
     * This constructor allows setting the timestamp of event.
     * 
     * @param device the device that originated the event
     * @param timestamp the timestamp of event
     */
    public IOEvent(IODevice device, long timestamp) {
        this.device = device;
        this.pin = null;
        this.value = 0;
        this.timestamp = timestamp;
        this.map = null;
    }

    /**
     * Constructs the event is relevant to a particular {@link Pin}.
     *
     * @param pin the pin that originated the event
     */
    public IOEvent(Pin pin) {
        this(pin, System.currentTimeMillis());
    }
    
    /**
     * Constructs the event is relevant to a particular {@link Pin}.
     *
     * This constructor allows setting the timestamp of event.
     * 
     * @param pin the pin that originated the event
     * @param timestamp the timestamp of event
     */
    public IOEvent(Pin pin, long timestamp) {
        this.device = pin.getDevice();
        this.pin = pin;
        this.value = pin.getValue();
        this.timestamp = timestamp;
        this.map = null;
    }

    public IOEvent(FirmataDevice device, Map<String, Object> map, long timestamp) {
        this.device = device ;
        this.pin = null;
        this.value = 0;
        this.timestamp = timestamp;
        this.map = map;
    }

    /**
     * Returns the device that relates to the event.
     *
     * @return the device in which the event has occurred
     */
    public IODevice getDevice() {
        return device;
    }

    /**
     * Returns the pin that relates to the event. If the event does not involve
     * any particular pin, it returns null.
     *
     * @return the pin that originated the event or null if a pin does not
     * involved in the event
     */
    public Pin getPin() {
        return pin;
    }

    /**
     * Returns the value the pin received.
     *
     * If the event is not about pin, always returns 0.
     * 
     * @return the value the pin received
     */
    public long getValue() {
        return value;
    }

    /**
     * Returns the timestamp of the event.
     *
     * @return timestamp of the event
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the map with key/values of none pin events.
     *
     * @return map with key/values
     */
    public Map<String, Object> getBody() {
        return map;
    }

    public Object getBodyItem(String key) {
        if (map == null) return null;
        return map.get(key);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (pin == null) {
            if (map == null) return "IOEvent [timestamp=" + timestamp + "]";
            StringBuilder sb = new StringBuilder();
            sb.append("IOEvent [");
            sb.append("timestamp=");
            sb.append(timestamp);
            for (String key : map.keySet()) {
                sb.append(", ");
                sb.append(key);
                sb.append("=");
                sb.append(map.get(key));
            }
            sb.append("]");
            return sb.toString();
        }
        else
            return "IOEvent [timestamp=" + timestamp + ", pin=" + pin + ", value=" + value + "]";
    }

     
}

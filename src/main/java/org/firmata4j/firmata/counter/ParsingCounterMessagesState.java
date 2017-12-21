/* 
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Thomas Welsch (ttww@gmx.de)
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

package org.firmata4j.firmata.counter;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;

import java.util.LinkedList;

import org.firmata4j.firmata.parser.FirmataToken;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.FiniteStateMachine;

/**
 * Implementation of the IRQ counter messages parser.
 *
 * @author Thomas Welsch &lt;ttww@gmx.de&gt;
 */
public class ParsingCounterMessagesState extends AbstractState {

    public ParsingCounterMessagesState(FiniteStateMachine fsm) {
        super(fsm);
    }

    // We use int, because we don't expect more than 2147483647 IRQs per time unit :-)
    private int readInt4(byte[] buf, int idx) {
        return (   (buf[idx + 0] & 0xff) << 24)
                | ((buf[idx + 1] & 0xff) << 16)
                | ((buf[idx + 2] & 0xff) << 8)
                |  (buf[idx + 3] & 0xff);
    }

    private LinkedList<Event> eventsToSend = new LinkedList<>();
    /*
     * (non-Javadoc)
     * 
     * @see org.firmata4j.fsm.State#process(byte)
     */
    @Override
    public void process(byte b) {

        if (b == END_SYSEX) {
            byte[] buffer = getBuffer();

            eventsToSend.clear();
            
            byte idx = 0;
            while  (idx < buffer.length) {
                byte pin   = buffer[idx++];
                long value = readInt4(buffer, idx);
                idx += 4;
                // We are using PIN_ID* keys, so the counter values can be send as pin change events:
                Event event = new Event(FirmataToken.COUNTER_MESSAGE, FirmataToken.FIRMATA_MESSAGE_EVENT_TYPE);
                event.setBodyItem(FirmataToken.PIN_ID, Byte.valueOf(pin));
                event.setBodyItem(FirmataToken.PIN_VALUE, Long.valueOf(value));
                eventsToSend.add(event);
            }

            transitTo(WaitingForMessageState.class);
            
            for (Event event : eventsToSend) {
                publish(event);
            }
            eventsToSend.clear();
        }
        else {
            bufferize(b);
        }
    }
}

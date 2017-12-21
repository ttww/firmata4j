
package org.firmata4j.firmata.counter;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;

import java.util.LinkedList;

import org.firmata4j.firmata.parser.FirmataToken;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.FiniteStateMachine;

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

//            System.err.println("Bits = " + Integer.toHexString((int) buffer[0] & 0xff));
//            System.err.println("Size = " + buffer.length);

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
        }
        else {
            bufferize(b);
        }
    }
}

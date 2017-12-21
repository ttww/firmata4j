
package org.firmata4j.firmata.counter;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;

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

    private Event[] eventsToSend = new Event[8];
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

            byte counterBits = buffer[0];
            
            byte counterBit = 1;
            byte idx = 1;
            for (byte bit = 0; bit < 8; bit++) {
                
                if ((counterBits & counterBit) != 0) {
                    int value = readInt4(buffer, idx);
                    idx += 4;

                    eventsToSend[bit] = new Event(FirmataToken.COUNTER_MESSAGE, FirmataToken.FIRMATA_MESSAGE_EVENT_TYPE);
                    eventsToSend[bit].setBodyItem(FirmataToken.COUNTER_ID, Byte.valueOf(bit));
                    eventsToSend[bit].setBodyItem(FirmataToken.COUNTER_VALUE, Integer.valueOf(value));
                }
                else {
                    eventsToSend[bit] = null;
                }
                
                counterBit <<= 1;
            }

            transitTo(WaitingForMessageState.class);
            
            for (byte bit = 0; bit < 8; bit++) {
                if (eventsToSend[bit] != null) {
                    publish(eventsToSend[bit]);
                    eventsToSend[bit] = null;
                }
            }
        }
        else {
            bufferize(b);

            // System.err.println("Receive " + Integer.toHexString((int) b & 0xff));
        }
    }
}

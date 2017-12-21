
package org.firmata4j.firmata.counter;

import org.firmata4j.Pin.Mode;
import org.firmata4j.firmata.parser.FirmataToken;
import org.firmata4j.firmata.parser.ParsingSysexMessageState;

public class CounterMessageFactory {

    // Load the parser if this class is used:
    static {
        ParsingSysexMessageState.addSysexMessageParser(
                FirmataToken.COUNTER_RESPONSE, ParsingCounterMessagesState.class);
    }



    // Config Command (COUNTER_CONFIG):
    // ==================================================================
    // [pin][config][sumTimeHighByte][sumTimeLowByte]
    //
    // [pin]:
    // Pin to config. We are using a extra byte after the COUNTER_CONFIG
    // to allow more pins.
    //
    // [config]:
    // 0x00 = OFF
    // 0x01 = CHANGE
    // 0x02 = RISING
    // 0x03 = FALLING
    // 0x11 = CHANGE with PULLUP
    // 0x12 = RISING with PULLUP
    // 0x13 = FALLING with PULLUP
    //
    // [sumTimeHighByte][sumTimeLowByte]:
    // Time in ms before report and reset the counter
    //
    // Query Command (COUNTER_QUERY)
    // ==================================================================
    // [counterBits][resetBits]
    // counterBits: Bit 0 = report counter 0, bit 1 for counter 1....
    // resetBits: Reset after reporting. Next automatic report after
    // sumTime ms if bit set.
    //
    // Reply Message (COUNTER_RESPONSE)
    // ==================================================================
    // [counterBits][first counter Big-Endian, 4 bytes unsigned integer]([second counter...])]
    // If more than one counter is reported, we start with the lowest counter.

    public static final byte COUNTER_CHANGE         = 0x00;
    public static final byte COUNTER_RISING         = 0x01;
    public static final byte COUNTER_FALLING        = 0x02;
    public static final byte COUNTER_CHANGE_PULLUP  = 0x03;
    public static final byte COUNTER_RISING_PULLUP  = 0x04;
    public static final byte COUNTER_FALLING_PULLUP = 0x05;

    /**
     * Creates Firmata message to configure IRQ counter pins.<br/>
     *
     * @param portId
     *            index of a port
     * @param config
     *            one of the COUNTER_* constants
     * @param collectTime
     *            time in ms for collection the interrupts
     *            
     * @return Firmata message to set counter
     */
    public static byte[] configureCounterPins(byte portId, byte config, int collectTime) {
        if (collectTime > 65535) {
            throw new IllegalArgumentException(
                    String.format(
                            "Pin %d collectTime for mode %s must be < 65536ms",
                            Byte.valueOf(portId), Mode.COUNTER
                    )
            );
        }
        if (config != COUNTER_CHANGE &&
            config != COUNTER_RISING &&
            config != COUNTER_FALLING &&
            config != COUNTER_CHANGE_PULLUP &&
            config != COUNTER_RISING_PULLUP &&
            config != COUNTER_FALLING_PULLUP) {
            throw new IllegalArgumentException(
                    String.format("Pin %d config for mode %s invalid.", Byte.valueOf(portId), Mode.COUNTER)
            );
        }


        return new byte[] {
                FirmataToken.START_SYSEX,
                FirmataToken.COUNTER_CONFIG,
                portId,
                config,
                (byte) ((collectTime >>> 8) & 0xff),
                (byte) (collectTime & 0xff),
                FirmataToken.END_SYSEX,
        };
        
        
    }

}

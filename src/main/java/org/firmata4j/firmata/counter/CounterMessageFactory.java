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

/**
 * Implementation of the IRQ counter messages and config factory.
 *
 * @author Thomas Welsch &lt;ttww@gmx.de&gt;
 */
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
    // 0x00 = CHANGE
    // 0x01 = RISING
    // 0x02 = FALLING
    // 0x03 = CHANGE with PULLUP
    // 0x04 = RISING with PULLUP
    // 0x05 = FALLING with PULLUP
    //
    // [sumTimeHighByte][sumTimeLowByte]:
    // Time in ms before report and reset the counter
    //
    // Reply Message (COUNTER_RESPONSE)
    // ==================================================================
    // [pin][first counter Big-Endian, 4 bytes unsigned integer]([pin...])]
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

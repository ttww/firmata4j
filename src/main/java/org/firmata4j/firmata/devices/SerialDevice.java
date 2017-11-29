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
 * Implementation of a serial firmata device.
 *
 * @author Thomas Welsch &lt;ttww@gmx.de&gt;
 */
package org.firmata4j.firmata.devices;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.firmata4j.firmata.FirmataDeviceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public class SerialDevice implements FirmataDeviceInterface, SerialPortEventListener {

    private SerialPort port;
    private BlockingQueue<byte[]> deviceReceiveQueue;
    private static final Logger LOGGER = LoggerFactory.getLogger(SerialDevice.class);

    /**
     *
     * @param portName
     */
    public SerialDevice(String portName) {
        port = new SerialPort(portName);
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#isOpened()
     */
    @Override
    public boolean isOpened() {
        return port.isOpened();
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#openPort(java.util.concurrent.BlockingQueue)
     */
    @Override
    public void openPort(BlockingQueue<byte[]> deviceReceiveQueue) throws IOException {
        try {
            port.openPort();
            port.setParams(
                    SerialPort.BAUDRATE_57600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            port.setEventsMask(SerialPort.MASK_RXCHAR);
            port.addEventListener(this);
            
            this.deviceReceiveQueue = deviceReceiveQueue;

        }
        catch (SerialPortException e) {
            throw new IOException("Can't setup serial port", e);
        }
        
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        // queueing data from input buffer to processing by FSM logic
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                while (!deviceReceiveQueue.offer(port.readBytes())) {
                    // trying to place bytes to queue until it succeeds
                }
            } catch (SerialPortException ex) {
                LOGGER.error("Cannot read from device", ex);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#writeBytes(byte[])
     */
    @Override
    public void writeBytes(byte[] msg) throws IOException {
        try {
            port.writeBytes(msg);
        } catch (SerialPortException ex) {
            throw new IOException("Cannot send message to device", ex);
        }
        
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#closePort()
     */
    @Override
    public void closePort() throws IOException {
        try {
            port.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
            port.closePort();
        } catch (SerialPortException ex) {
            throw new IOException("Cannot properly stop firmata device", ex);
        }

    }


}

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
package org.firmata4j.firmata.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

import org.firmata4j.firmata.FirmataTransportInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Interface to abstract the device connection (serial/network) to a firmata device.
 *
 * @author Thomas Welsch &lt;ttww@gmx.de&gt;
 */
public class Network implements FirmataTransportInterface {

    private BlockingQueue<byte[]> deviceReceiveQueue;
    private static final Logger LOGGER = LoggerFactory.getLogger(Network.class);

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private InetAddress ip;
    private int port;

    private  PacketCounter packetCounter = new PacketCounter();
    
    /**
     *
     * @param ip
     * @param port
     */
    public Network(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#isOpened()
     */
    @Override
    public boolean isOpened() {
//        System.err.println("Socket = " + socket);
        if (socket == null) return false;
//        System.err.println("Socket.isConnected()      = " + socket.isConnected());
//        System.err.println("Socket.isInputShutdown()  = " + socket.isInputShutdown());
//        System.err.println("Socket.isOutputShutdown() = " + socket.isOutputShutdown());
//        System.err.println("Socket.isBound()          = " + socket.isBound());
//        System.err.println("Socket.isClosed()         = " + socket.isClosed());
        return !socket.isClosed();
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#openPort(java.util.concurrent.BlockingQueue)
     */
    @Override
    public void openPort(@SuppressWarnings("hiding") BlockingQueue<byte[]> deviceReceiveQueue) throws IOException {
        connect();
      
        this.deviceReceiveQueue = deviceReceiveQueue;
        
        ReaderThread reader = new ReaderThread();
        reader.start();
    }

    private void connect() throws IOException {
        socket  = new Socket(ip, port);
        socket.setReuseAddress(true);
        socket.setSoTimeout(1500);
        socket.setSoLinger(true, 1500);
        socket.setSoTimeout(1500);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
//        System.err.println("Connect done: " + socket.isBound() + " = " + socket);
    }

    class ReaderThread extends Thread {

        public ReaderThread() {
            setDaemon(true);
            setName("ReaderThread for " + ip.getHostAddress() + ":" + port);
        }
        
        @Override
        public void run() {
            byte[] buf = new byte[100];
            
            LOGGER.debug("Start reader");

            try {
                while (true) {
                    int readIn;
                    try {
                        readIn = in.read(buf);
                    }
                    
                    catch (SocketTimeoutException e) {
                        break;  // We try to reconnect, hearthbeats (1*second) missing
                    }
                    if (readIn == -1) {
                        break;  // Connection closed
                    }
                    
                    packetCounter.count(readIn);
                    
                    // Copy the data to new buffer
                    byte[] data = new byte[readIn];
                    for (int i=0; i<readIn; i++) data[i] = buf[i];
                    
                    deviceReceiveQueue.offer(data);
                }   // while
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.debug("Stop reader");
            try {
                closePort();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#writeBytes(byte[])
     */
    @Override
    public void writeBytes(byte[] msg) throws IOException {
        out.write(msg);
    }

    /* (non-Javadoc)
     * @see org.firmata4j.firmata.FirmataDeviceInterface#closePort()
     */
    @Override
    public void closePort() throws IOException {
        LOGGER.debug("closePort()");
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
        out = null;
        in = null;
        socket = null;
    }


}

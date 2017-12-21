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

package org.firmata4j.firmata.transport;


/**
 * Helper class to calculate package load.
 *
 * @author Thomas Welsch &lt;ttww@gmx.de&gt;
 */
public class PacketCounter {

    private long lastSecond;
    
    private int  packetsPerSecond;
    private int  maxPacketsPerSecond;
    
    private int  bytesPerSecond;
    private int  maxBytesPerSecond;
    
    private long bytesRead;
    private long packagesRead;
    
    private void checkSeconds() {
        // System.err.print(".");
        long nowSecond = System.currentTimeMillis() / 1000;
        if (nowSecond != lastSecond) {
            if (packetsPerSecond > maxPacketsPerSecond) {
                maxPacketsPerSecond = packetsPerSecond;
                // System.err.println("New max packets = " + maxPacketsPerSecond);
            }
            if (bytesPerSecond > maxBytesPerSecond) {
                maxBytesPerSecond = bytesPerSecond;
                // System.err.println("New max bytes = " + maxBytesPerSecond);
            }
            
            // System.err.println(" (" + packetsPerSecond + ")");
            lastSecond = nowSecond;
            packetsPerSecond = 0;
            bytesPerSecond = 0;
        }
    }
    
    public void count(int len) {
        checkSeconds();
        
        bytesRead += len;
        packagesRead++;
        
        packetsPerSecond++;
        bytesPerSecond += len;
    }
    
    public int getPacketsPerSecond() {
        checkSeconds();
        return packetsPerSecond;
    }

    public int getBytesPerSecond() {
        checkSeconds();
        return bytesPerSecond;
    }

    public int getMaxPacketsPerSecond() {
        return maxPacketsPerSecond;
    }
    
    public int getMaxBytesPerSecond() {
        return maxBytesPerSecond;
    }
    
    public long getAbsoluteBytesRead() {
        return bytesRead;
    }
    
    public long getAbsolutePackagesRead() {
        return packagesRead;
    }
    
    public void reset() {
        lastSecond          = 0;
        packetsPerSecond    = 0;
        maxPacketsPerSecond = 0;
        bytesPerSecond      = 0;
        maxBytesPerSecond   = 0;
        bytesRead           = 0;
        packagesRead        = 0;
    }
    
}

package org.firmata4j.firmata.transport;


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

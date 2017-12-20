package org.firmata4j.boards;


public class NodeMCUBoard implements BoardInterface {

    @Override
    public String getBoardName() {
        return "Amica NodeMCU V2";
    }

    private static final String pinNames[] = {
            "D3",   // 0
            "D10",  // 1
            "D4",   // 2
            "D9",   // 3
            "D2",   // 4
            "D1",   // 5
            "na",   // 6
            "na",   // 7
            "na",   // 8
            "na",   // 9
            "na",   // 10
            "na",   // 11
            "D6",   // 12
            "D7",   // 13
            "D5",   // 14
            "D8",   // 15
            "D0",   // 16
            "ADC0",   // 17
    };

    private static final String pinArduino[] = {
            "GPIO0",   // 0
            "GPIO1",  // 1
            "GPIO2",   // 2
            "GPIO3",   // 3
            "GPIO4",   // 4
            "GPIO5",   // 5
            "na",   // 6
            "na",   // 7
            "na",   // 8
            "na",   // 9
            "na",   // 10
            "na",   // 11
            "GPIO12",   // 12
            "GPIO13",   // 13
            "GPIO14",   // 14
            "GPIO15",   // 15
            "GPIO16",   // 16
            "ADC0",   // 17
    };

    private static final String pinSpecialFunction[] = {
            "USER",   // 0          (Button)
            "Serial 0 TXD",  // 1
            "LED inverted",   // 2
            "Serial 0 RXD",   // 3
            "I2C SDA",   // 4
            "I2C SCL",   // 5
            "na",   // 6
            "na",   // 7
            "na",   // 8
            "na",   // 9
            "na",   // 10
            "na",   // 11
            "SPI MISO",   // 12
            "SPI MOSI",   // 13
            "SPI SCLK",   // 14
            "SPI SS",   // 15
            "ADC0",   // 16
    };

    @Override
    public String getPinName(int pin) {
        return pinNames[pin];
    }

    @Override
    public String getArduinoPinName(int pin) {
        return pinArduino[pin];
    }

    @Override
    public String getSpecialFunctionPinName(int pin) {
        return pinSpecialFunction[pin];
    }

}

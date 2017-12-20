package org.firmata4j.boards;


public interface BoardInterface {

    String getBoardName();
    
    String getPinName(int pin);

    String getArduinoPinName(int pin);
    
    String getSpecialFunctionPinName(int pin);
}

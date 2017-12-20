package org.firmata4j.boards;


public class UnknownBoard implements BoardInterface {

    @Override
    public String getBoardName() {
        return "Unknown board";
    }

    @Override
    public String getPinName(int pin) {
        return "#" + pin;
    }

    @Override
    public String getArduinoPinName(int pin) {
        return "? " + pin;
    }

    @Override
    public String getSpecialFunctionPinName(int pin) {
        return "? " + pin;
    }

}

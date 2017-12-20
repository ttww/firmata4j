
package org.firmata4j.firmata.counter;

import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.FiniteStateMachine;

public class ParsingCounterMessagesState extends AbstractState {

    public ParsingCounterMessagesState(FiniteStateMachine fsm) {
        super(fsm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.firmata4j.fsm.State#process(byte)
     */
    @Override
    public void process(byte b) {
        // TODO Auto-generated method stub


    }
}

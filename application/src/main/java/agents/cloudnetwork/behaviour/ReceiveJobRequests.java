package agents.cloudnetwork.behaviour;

import jade.core.behaviours.CyclicBehaviour;

/**
 * Behaviour which is responsible for handling upcoming call for proposals from clients
 * (instead of HandleClientCallForProposal)
 */
public class ReceiveJobRequests extends CyclicBehaviour {

    
    @Override
    public void action() {
        // get the CFP messages from clients and create CFP messages for servers
        // TODO implement proper message receiving
        var receivedMessage = myAgent.receive(); 
        myAgent.addBehaviour(new AnnounceNewJobRequest(myAgent, receivedMessage));
    }
}

package agents.client.behaviour;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour responsible for finding cloud network agents for communication
 */
public class FindCloudNetworkAgents extends OneShotBehaviour {

    @Override
    public void action() {
        
        getParent().getDataStore(); // <--- here put found cna's
    }   
}

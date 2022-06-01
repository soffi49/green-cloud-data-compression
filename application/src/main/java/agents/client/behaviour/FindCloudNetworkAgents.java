package agents.client.behaviour;

import static agents.client.ClientAgentConstants.CLOUD_NETWORK_AGENTS;
import static common.constant.DFServiceConstants.CNA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.search;

import agents.client.ClientAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Behaviour responsible for finding cloud network agents for communication
 */
public class FindCloudNetworkAgents extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FindCloudNetworkAgents.class);
    private ClientAgent myClientAgent;

    @Override
    public void onStart() {
        super.onStart();
        this.myClientAgent = (ClientAgent) myAgent;
    }

    @Override
    public void action() {
        final List<AID> cloudNetworkAgents = search(myAgent, CNA_SERVICE_TYPE);

        if(cloudNetworkAgents.isEmpty()) {
            logger.info("[{}] No Cloud Network Agents were found", myClientAgent);
            myClientAgent.doDelete();
        }
        getParent().getDataStore().put(CLOUD_NETWORK_AGENTS, cloudNetworkAgents);
    }   
}

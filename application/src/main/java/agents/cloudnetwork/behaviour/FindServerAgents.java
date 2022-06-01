package agents.cloudnetwork.behaviour;

import agents.cloudnetwork.CloudNetworkAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static agents.cloudnetwork.CloudNetworkAgentConstants.SERVER_AGENTS;
import static common.constant.DFServiceConstants.SA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.search;

/**
 * Behaviours which is responsible for finding corresponding server agents for given CNA in DF
 */
public class FindServerAgents extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FindServerAgents.class);

    private CloudNetworkAgent myCloudNetworkAgent;

    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    public void action() {
        final List<AID> serverAgents = search(myAgent, SA_SERVICE_TYPE, myAgent.getName());

        if (serverAgents.isEmpty()) {
            logger.info("[{}] No Server Agents were found", myCloudNetworkAgent);
            myCloudNetworkAgent.doDelete();
        }
        getParent().getDataStore().put(SERVER_AGENTS, serverAgents);
    }
}

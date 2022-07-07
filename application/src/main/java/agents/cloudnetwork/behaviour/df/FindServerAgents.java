package agents.cloudnetwork.behaviour.df;

import agents.cloudnetwork.CloudNetworkAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static common.constant.DFServiceConstants.SA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.search;

/**
 * Behaviours which is responsible for finding corresponding server agents for given Cloud Network Agent in DF
 */
public class FindServerAgents extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FindServerAgents.class);

    private CloudNetworkAgent myCloudNetworkAgent;

    /**
     * Method runs at the behaviours start. It casts the agent to the agent of type CloudNetworkAgent
     */
    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    /**
     * Method searches the Directory Facilitator for the corresponding to the given Cloud Network Agent servers
     */
    @Override
    public void action() {
        final List<AID> serverAgents = search(myAgent, SA_SERVICE_TYPE, myAgent.getName());

        if (serverAgents.isEmpty()) {
            logger.info("[{}] No Server Agents were found", myCloudNetworkAgent.getName());
            myCloudNetworkAgent.doDelete();
        }
        myCloudNetworkAgent.setOwnedServers(serverAgents);
    }
}

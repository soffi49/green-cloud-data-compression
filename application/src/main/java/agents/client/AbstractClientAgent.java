package agents.client;

import jade.core.AID;
import jade.core.Agent;

/**
 * Abstract agent class storing the data regarding Client Agent
 */
public class AbstractClientAgent extends Agent {

    protected AID chosenCloudNetworkAgent;

    public AbstractClientAgent() {
    }

    /**
     * Abstract Client Agent constructor.
     *
     * @param chosenCloudNetworkAgent Cloud Network Agent that was chosen for the job execution
     */
    public AbstractClientAgent(AID chosenCloudNetworkAgent) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
    }

    public AID getChosenCloudNetworkAgent() {
        return chosenCloudNetworkAgent;
    }

    public void setChosenCloudNetworkAgent(AID chosenCloudNetworkAgent) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
    }
}

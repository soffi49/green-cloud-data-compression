package agents.client;

import jade.core.AID;
import jade.core.Agent;

public class AbstractClientAgent extends Agent {

    protected AID chosenCloudNetworkAgent;

    public AbstractClientAgent() {
    }

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

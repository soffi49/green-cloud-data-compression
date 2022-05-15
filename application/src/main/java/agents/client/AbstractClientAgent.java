package agents.client;

import jade.core.AID;
import jade.core.Agent;

public class AbstractClientAgent extends Agent {

    protected AID chosenCloudNetworkAgent;
    protected int messagesSentCount;

    public AbstractClientAgent() {
    }

    public AbstractClientAgent(AID chosenCloudNetworkAgent, int messagesSentCount) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
        this.messagesSentCount = messagesSentCount;
    }

    public AID getChosenCloudNetworkAgent() {
        return chosenCloudNetworkAgent;
    }

    public void setChosenCloudNetworkAgent(AID chosenCloudNetworkAgent) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public void setMessagesSentCount(int messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
    }
}

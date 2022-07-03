package agents.client;

import agents.AbstractAgent;
import jade.core.AID;

import java.time.OffsetDateTime;

/**
 * Abstract agent class storing the data regarding Client Agent
 */
public abstract class AbstractClientAgent extends AbstractAgent {

    protected AID chosenCloudNetworkAgent;
    protected OffsetDateTime simulatedJobStart;
    protected OffsetDateTime simulatedJobEnd;

    public AbstractClientAgent() {
        super.setup();
    }

    /**
     * Abstract Client Agent constructor.
     *
     * @param chosenCloudNetworkAgent Cloud Network Agent that was chosen for the job execution
     * @param simulatedJobStart       time when the job execution should start in the simulation
     * @param simulatedJobEnd         time when the job execution should end in the simulation
     */
    AbstractClientAgent(AID chosenCloudNetworkAgent,
                        OffsetDateTime simulatedJobStart,
                        OffsetDateTime simulatedJobEnd) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
        this.simulatedJobStart = simulatedJobStart;
        this.simulatedJobEnd = simulatedJobEnd;
    }

    public AID getChosenCloudNetworkAgent() {
        return chosenCloudNetworkAgent;
    }

    public void setChosenCloudNetworkAgent(AID chosenCloudNetworkAgent) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
    }

    public OffsetDateTime getSimulatedJobStart() {
        return simulatedJobStart;
    }

    public void setSimulatedJobStart(OffsetDateTime simulatedJobStart) {
        this.simulatedJobStart = simulatedJobStart;
    }

    public OffsetDateTime getSimulatedJobEnd() {
        return simulatedJobEnd;
    }

    public void setSimulatedJobEnd(OffsetDateTime simulatedJobEnd) {
        this.simulatedJobEnd = simulatedJobEnd;
    }
}

package agents.client;

import java.time.Instant;

import agents.AbstractAgent;
import jade.core.AID;

/**
 * Abstract agent class storing the data regarding Client Agent
 */
public abstract class AbstractClientAgent extends AbstractAgent {

	protected AID chosenCloudNetworkAgent;
	protected Instant simulatedJobStart;
	protected Instant simulatedJobEnd;
	protected Integer retries;
	protected boolean announced;

	public AbstractClientAgent() {
		super.setup();
		retries = 0;
	}

	/**
	 * Abstract Client Agent constructor.
	 *
	 * @param chosenCloudNetworkAgent Cloud Network Agent that was chosen for the job execution
	 * @param simulatedJobStart       time when the job execution should start in the simulation
	 * @param simulatedJobEnd         time when the job execution should end in the simulation
	 */
	AbstractClientAgent(AID chosenCloudNetworkAgent,
			Instant simulatedJobStart,
			Instant simulatedJobEnd) {
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

	public Instant getSimulatedJobStart() {
		return simulatedJobStart;
	}

	public void setSimulatedJobStart(Instant simulatedJobStart) {
		this.simulatedJobStart = simulatedJobStart;
	}

	public Instant getSimulatedJobEnd() {
		return simulatedJobEnd;
	}

	public void setSimulatedJobEnd(Instant simulatedJobEnd) {
		this.simulatedJobEnd = simulatedJobEnd;
	}

	public Integer getRetries() {
		return retries;
	}

	public void retry() {
		retries++;
	}

	public void announce() {
		announced = true;
	}

	public boolean isAnnounced() {
		return announced;
	}
}

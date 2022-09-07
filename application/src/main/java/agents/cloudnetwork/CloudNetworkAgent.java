package agents.cloudnetwork;

import static common.constant.DFServiceConstants.CNA_SERVICE_NAME;
import static common.constant.DFServiceConstants.CNA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.cloudnetwork.behaviour.ReceiveJobRequests;
import agents.cloudnetwork.behaviour.df.FindServerAgents;
import agents.cloudnetwork.behaviour.jobstatus.ReturnJobStatusUpdate;
import agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerJobTransferRequest;
import agents.cloudnetwork.domain.CloudNetworkStateManagement;
import common.behaviours.ReceiveGUIController;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;

import java.util.Collections;
import java.util.List;

/**
 * Agent representing the Cloud Network Agent that handles part of the Cloud Network
 */
public class CloudNetworkAgent extends AbstractCloudNetworkAgent {

	/**
	 * Method run at the agent's start. In initialize the Cloud Network Agent based on the given by the user arguments and
	 * runs the starting behaviours - looking up the corresponding servers and listening for the job requests.
	 */
	@Override
	protected void setup() {
		super.setup();
		initializeAgent();
		addBehaviour(new ReceiveGUIController(this, prepareBehaviours()));
	}

	@Override
	protected void takeDown() {
		super.takeDown();
	}

	private void initializeAgent() {
		register(this, CNA_SERVICE_TYPE, CNA_SERVICE_NAME);
		this.stateManagement = new CloudNetworkStateManagement(this);
	}

	private List<Behaviour> prepareBehaviours() {
		final ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		parallelBehaviour.addSubBehaviour(prepareStartingBehaviour());
		parallelBehaviour.addSubBehaviour(new ReturnJobStatusUpdate());
		parallelBehaviour.addSubBehaviour(new ListenForServerJobTransferRequest());
		return Collections.singletonList(parallelBehaviour);
	}

	private SequentialBehaviour prepareStartingBehaviour() {
		var startingBehaviour = new SequentialBehaviour(this);
		startingBehaviour.addSubBehaviour(new FindServerAgents());
		startingBehaviour.addSubBehaviour(new ReceiveJobRequests());
		return startingBehaviour;
	}
}

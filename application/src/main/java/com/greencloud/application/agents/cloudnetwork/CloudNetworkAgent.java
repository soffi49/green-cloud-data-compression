package com.greencloud.application.agents.cloudnetwork;

import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_TYPE;

import java.util.List;

import com.greencloud.application.agents.cloudnetwork.behaviour.df.FindSchedulerAndServerAgents;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.ListenForNetworkChange;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.ListenForServerDisabling;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForCloudNetworkJobCancellation;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForJobStatusChange;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForScheduledJob;
import com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerJobTransferRequest;
import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkConfigManagement;
import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkStateManagement;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

/**
 * Agent representing the network component that orchestrates work in part of the Cloud Network
 */
public class CloudNetworkAgent extends AbstractCloudNetworkAgent {

	@Override
	protected void initializeAgent(final Object[] args) {
		register(this, CNA_SERVICE_TYPE, CNA_SERVICE_NAME);

		this.stateManagement = new CloudNetworkStateManagement(this);
		this.configManagement = new CloudNetworkConfigManagement(this);
		this.maximumCapacity = args[0] != null ? Double.parseDouble(args[0].toString()) : 0.0;
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return List.of(
				prepareDFBehaviour(),
				new ListenForJobStatusChange(),
				new ListenForServerJobTransferRequest(),
				new ListenForCloudNetworkJobCancellation(),
				new ListenForNetworkChange(),
				new ListenForServerDisabling(this)
		);
	}

	private SequentialBehaviour prepareDFBehaviour() {
		var startingBehaviour = new SequentialBehaviour(this);
		startingBehaviour.addSubBehaviour(new FindSchedulerAndServerAgents());
		startingBehaviour.addSubBehaviour(new ListenForScheduledJob());
		return startingBehaviour;
	}
}

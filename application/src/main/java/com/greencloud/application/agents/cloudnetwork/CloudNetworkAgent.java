package com.greencloud.application.agents.cloudnetwork;

import static com.greencloud.application.domain.agent.enums.AgentManagementEnum.STATE_MANAGEMENT;
import static com.greencloud.application.yellowpages.YellowPagesService.deregister;
import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_TYPE;

import java.util.List;

import com.google.common.util.concurrent.AtomicDouble;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.ListenForServerDisabling;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.subscribe.FindSchedulerAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.subscribe.SubscribeServerService;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForCloudNetworkJobCancellation;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForJobStatusChange;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForScheduledJob;
import com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerJobTransferRequest;
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
		this.maximumCapacity = new AtomicDouble(0.0);
	}

	@Override
	protected void initializeAgentManagements() {
		this.agentManagementServices.put(STATE_MANAGEMENT, new CloudNetworkStateManagement(this));
	}

	@Override
	protected void takeDown() {
		deregister(this, CNA_SERVICE_TYPE, CNA_SERVICE_NAME);
		super.takeDown();
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return List.of(
				prepareDFBehaviour(),
				SubscribeServerService.create(this),
				new ListenForJobStatusChange(),
				new ListenForServerJobTransferRequest(),
				new ListenForCloudNetworkJobCancellation(),
				new ListenForServerDisabling(this)
		);
	}

	private SequentialBehaviour prepareDFBehaviour() {
		var startingBehaviour = new SequentialBehaviour(this);
		startingBehaviour.addSubBehaviour(new FindSchedulerAgent());
		startingBehaviour.addSubBehaviour(new ListenForScheduledJob());
		return startingBehaviour;
	}
}

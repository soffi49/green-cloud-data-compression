package com.greencloud.application.agents.client.behaviour.jobannouncement.initiator;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.SEND_INFORM_TO_SCHEDULER;
import static com.greencloud.application.agents.client.constants.ClientAgentConstants.SCHEDULER_AGENT;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.prepareJobAnnouncementMessage;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.greencloud.application.agents.client.ClientAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour sends the information announcing new job to Scheduler Agent
 */
public class InitiateNewJobAnnouncement extends OneShotBehaviour {

	private static final Logger logger = getLogger(InitiateNewJobAnnouncement.class);

	private final ClientAgent myClientAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent agent executing the behaviour
	 */
	public InitiateNewJobAnnouncement(final Agent agent) {
		this.myClientAgent = (ClientAgent) agent;
	}

	@Override
	public void action() {
		logger.info(SEND_INFORM_TO_SCHEDULER);
		final AID schedulerAgent = (AID) getParent().getDataStore().get(SCHEDULER_AGENT);
		myClientAgent.send(prepareJobAnnouncementMessage(schedulerAgent, myClientAgent.getJobExecution().getJob()));
	}
}

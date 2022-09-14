package com.greencloud.application.agents.server.behaviour.jobexecution.listener;

import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.jobexecution.initiator.InitiatePowerDeliveryForJob;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.templates.JobHandlingMessageTemplates;
import com.greencloud.application.domain.job.Job;
import com.greencloud.application.domain.job.JobStatusEnum;
import com.greencloud.application.mapper.JobMapper;
import com.greencloud.application.messages.MessagingUtils;
import com.greencloud.application.messages.domain.constants.MessageProtocolConstants;
import com.greencloud.application.messages.domain.factory.CallForProposalMessageFactory;
import com.greencloud.application.messages.domain.factory.ReplyMessageFactory;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles upcoming job's CFP from cloud network com.greencloud.application.agents
 */
public class ListenForNewJob extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForNewJob.class);

	private ServerAgent myServerAgent;
	private String guid;

	/**
	 * Method casts the agent to the ServerAgent.
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myServerAgent = (ServerAgent) myAgent;
		this.guid = myServerAgent.getName();
	}

	/**
	 * Method listens for the upcoming job CFP coming from the Cloud Network Agents.
	 * It validates whether the server has enough power to handle the job.
	 * If yes, then it sends the CFP to owned green sources to find available power sources.
	 * If no, then it sends the refuse message to the Cloud Network Agent.
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(JobHandlingMessageTemplates.NEW_JOB_CFP_TEMPLATE);

		if (Objects.nonNull(message)) {
			final Job job = MessagingUtils.readMessageContent(message, Job.class);
			final int availableCapacity = myServerAgent.manage()
					.getAvailableCapacity(job.getStartTime(), job.getEndTime(), null, null);
			final boolean validJobConditions = job.getPower() <= availableCapacity &&
					!myServerAgent.getServerJobs().containsKey(job) &&
					myServerAgent.canTakeIntoProcessing();

			if (validJobConditions) {
				initiateNegotiationWithPowerSources(job, message);
			} else {
				logger.info(JobHandlingListenerLog.SERVER_NEW_JOB_LACK_OF_POWER_LOG, guid);
				displayMessageArrow(myServerAgent, message.getSender());
				myAgent.send(ReplyMessageFactory.prepareRefuseReply(message.createReply()));
			}
		} else {
			block();
		}
	}

	private void initiateNegotiationWithPowerSources(final Job job, final ACLMessage cnaMessage) {
		logger.info(JobHandlingListenerLog.SERVER_NEW_JOB_LOOK_FOR_SOURCE_LOG, guid);
		myServerAgent.getServerJobs().putIfAbsent(job, JobStatusEnum.PROCESSING);
		myServerAgent.tookJobIntoProcessing();

		final ACLMessage cfp = CallForProposalMessageFactory.createCallForProposal(JobMapper.mapJobToPowerJob(job),
				myServerAgent.getOwnedGreenSources(), MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL);

		displayMessageArrow(myServerAgent, myServerAgent.getOwnedGreenSources());
		myAgent.addBehaviour(new InitiatePowerDeliveryForJob(myAgent, cfp, cnaMessage.createReply(), job));
	}
}

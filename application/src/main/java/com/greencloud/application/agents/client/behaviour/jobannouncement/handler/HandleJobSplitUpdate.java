package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.handler.logs.JobAnnouncementHandlerLog.CLIENT_JOB_SPLIT_LOG;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.domain.ClientJobExecution;
import com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum;
import com.greencloud.application.domain.job.JobParts;
import com.greencloud.application.mapper.JobMapper;
import com.greencloud.commons.domain.job.ClientJob;
import com.gui.agents.ClientAgentNode;

import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles information that a given job is to be split
 */
public class HandleJobSplitUpdate extends AbstractJobUpdateHandler {

	private static final Logger logger = getLogger(HandleJobSplitUpdate.class);

	public HandleJobSplitUpdate(final ACLMessage message, final ClientAgent myClient,
			final ClientJobUpdateEnum updateEnum) {
		super(message, myClient, updateEnum);
	}

	/**
	 * Method splits the client job into parts and passes the information to GUI
	 */
	@Override
	public void action() {
		logger.info(CLIENT_JOB_SPLIT_LOG);

		final JobParts splitJob = readMessageContent(message, JobParts.class);
		final List<ClientJob> jobParts = splitJob.getJobParts();

		myClient.split();
		jobParts.forEach(jobPart ->
				myClient.getJobParts().put(jobPart.getJobId(), new ClientJobExecution(jobPart,
						myClient.getJobExecution().getJobSimulatedStart(), myClient.getJobExecution().getJobSimulatedEnd(),
						myClient.getJobExecution().getJobSimulatedDeadline(), CREATED)));

		((ClientAgentNode) myClient.getAgentNode())
				.informAboutSplitJob(jobParts.stream().map(JobMapper::mapToClientJobRealTime).toList());
	}
}

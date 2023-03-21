package com.greencloud.application.agents.server.behaviour.jobexecution.handler;

import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_ALREADY_STARTED_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_START_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_START_NO_GREEN_SOURCE_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_START_NO_INFORM_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_START_NO_PRESENT_LOG;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.constants.MessageConversationConstants.BACK_UP_POWER_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.ON_HOLD_JOB_ID;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobStartedMessage;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.replaceStatusToActive;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PLANNED_JOB_STATUSES;
import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour handles job execution start
 */
public class HandleJobStart extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleJobStart.class);

	private final ServerAgent myServerAgent;
	private final ClientJob jobToExecute;
	private final boolean informCNAStart;
	private final boolean informCNAFinish;

	private HandleJobStart(final ServerAgent agent, final Date startDate, final ClientJob job,
			final boolean informCNAStart, final boolean informCNAFinish) {
		super(agent, startDate);

		this.jobToExecute = job;
		this.myServerAgent = agent;
		this.informCNAStart = informCNAStart;
		this.informCNAFinish = informCNAFinish;
	}

	/**
	 * Method calculates the time after which the job execution will start.
	 * If the provided time is later than the current time then the job execution will start immediately
	 *
	 * @param serverAgent     agent that will execute the behaviour
	 * @param job             job to execute
	 * @param informCNAStart  flag indicating whether the cloud network should be informed about job start
	 * @param informCNAFinish flag indicating whether the cloud network should be informed about job finish
	 * @return HandleJobStart
	 */
	public static HandleJobStart createFor(final ServerAgent serverAgent, final ClientJob job,
			final boolean informCNAStart, final boolean informCNAFinish) {
		final Instant startDate = getCurrentTime().isAfter(job.getStartTime()) ? getCurrentTime() : job.getStartTime();
		return new HandleJobStart(serverAgent, Date.from(startDate), job, informCNAStart, informCNAFinish);
	}

	/**
	 * Method starts the execution of the job.
	 * It updates the server state, then sends the information that the execution has started to the
	 * Green Source Agent and the Cloud Network.
	 * Finally, it schedules the behaviour executed upon job execution finish.
	 */
	@Override
	protected void onWake() {
		final String jobId = jobToExecute.getJobId();
		MDC.put(MDC_JOB_ID, jobId);

		if (!myServerAgent.getServerJobs().containsKey(jobToExecute)) {
			logger.info(JOB_START_NO_PRESENT_LOG, jobId);
			return;
		} else if (!myServerAgent.getGreenSourceForJobMap().containsKey(jobId)) {
			logger.info(JOB_START_NO_GREEN_SOURCE_LOG, jobId);
			return;
		}

		if (PLANNED_JOB_STATUSES.contains(myServerAgent.getServerJobs().getOrDefault(jobToExecute, ACCEPTED))) {
			final String logMessage = informCNAStart ? JOB_START_LOG : JOB_START_NO_INFORM_LOG;
			logger.info(logMessage, jobId);

			sendJobStartMessage();
			substituteJobStatus();
			myServerAgent.manage().incrementJobCounter(mapToJobInstanceId(jobToExecute), STARTED);
			myAgent.addBehaviour(HandleJobFinish.createFor(myServerAgent, jobToExecute, informCNAFinish));
		} else {
			logger.info(JOB_ALREADY_STARTED_LOG, jobId);
		}
	}

	private void sendJobStartMessage() {
		final AID greenSource = myServerAgent.getGreenSourceForJobMap().get(jobToExecute.getJobId());
		final List<AID> receivers = informCNAStart ?
				List.of(greenSource, myServerAgent.getOwnerCloudNetworkAgent()) :
				singletonList(greenSource);

		myAgent.send(prepareJobStartedMessage(jobToExecute, receivers.toArray(new AID[0])));
	}

	private void substituteJobStatus() {
		final JobExecutionStatusEnum currentStatus = myServerAgent.getServerJobs().get(jobToExecute);
		final JobInstanceIdentifier jobInstance = mapToJobInstanceId(jobToExecute);

		replaceStatusToActive(myServerAgent.getServerJobs(), jobToExecute);
		myServerAgent.message().informCNAAboutStatusChange(jobInstance, getStatus(currentStatus));
	}

	private String getStatus(final JobExecutionStatusEnum currentStatus) {
		return switch (currentStatus) {
			case ACCEPTED -> GREEN_POWER_JOB_ID;
			case ON_HOLD_SOURCE_SHORTAGE_PLANNED, ON_HOLD_PLANNED, ON_HOLD_TRANSFER_PLANNED -> ON_HOLD_JOB_ID;
			case IN_PROGRESS_BACKUP_ENERGY_PLANNED -> BACK_UP_POWER_JOB_ID;
			default -> null;
		};
	}
}

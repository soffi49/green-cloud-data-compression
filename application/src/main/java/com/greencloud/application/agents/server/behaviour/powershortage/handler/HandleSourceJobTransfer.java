package com.greencloud.application.agents.server.behaviour.powershortage.handler;

import static com.greencloud.application.agents.server.behaviour.powershortage.handler.logs.PowerShortageServerHandlerLog.GS_TRANSFER_EXECUTION_LOG;
import static com.greencloud.application.messages.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobStartedMessage;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndEndDate;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDate;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.commons.constants.LoggingConstant;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour transfers a job to a new green source
 */
public class HandleSourceJobTransfer extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleSourceJobTransfer.class);

	private final ServerAgent myServerAgent;
	private final JobInstanceIdentifier jobInstanceId;
	private final AID newGreenSource;

	private HandleSourceJobTransfer(final ServerAgent myAgent, final Date transferTime,
			final JobInstanceIdentifier jobInstanceId, final AID newGreenSource) {
		super(myAgent, transferTime);

		this.myServerAgent = myAgent;
		this.jobInstanceId = jobInstanceId;
		this.newGreenSource = newGreenSource;
	}

	/**
	 * Method creating the behaviour
	 *
	 * @param serverAgent    server executing the behaviour
	 * @param jobInstance    unique identifier of the job instance
	 * @param newGreenSource green source which will execute the job after power shortage
	 * @return behaviour which transfer the jobs between green sources
	 */
	public static HandleSourceJobTransfer create(final ServerAgent serverAgent, final JobInstanceIdentifier jobInstance,
			final AID newGreenSource) {
		final Instant transferTime = getCurrentTime().isAfter(jobInstance.getStartTime()) ?
				getCurrentTime() :
				jobInstance.getStartTime();

		return new HandleSourceJobTransfer(serverAgent, Date.from(transferTime), jobInstance, newGreenSource);
	}

	/**
	 * Method transfers the job between green sources.
	 * It updates the internal server state.
	 */
	@Override
	protected void onWake() {
		final ClientJob previousInstance = getJobByIdAndEndDate(jobInstanceId.getJobId(), jobInstanceId.getStartTime(),
				myServerAgent.getServerJobs());
		final ClientJob jobToExecute = getJobByIdAndStartDate(jobInstanceId, myServerAgent.getServerJobs());

		MDC.put(LoggingConstant.MDC_JOB_ID, jobInstanceId.getJobId());
		finishPreviousInstance(previousInstance);

		if (nonNull(jobToExecute)) {
			logger.info(GS_TRANSFER_EXECUTION_LOG);
			myServerAgent.getGreenSourceForJobMap().replace(jobToExecute.getJobId(), newGreenSource);
			myServerAgent.message().informCNAAboutStatusChange(jobInstanceId, GREEN_POWER_JOB_ID);
			myServerAgent.manage().updateGUI();

			if (isJobStarted(jobToExecute, myServerAgent.getServerJobs())) {
				myAgent.send(prepareJobStartedMessage(jobToExecute.getJobId(), jobToExecute.getStartTime(),
						newGreenSource));
			}
		}
	}

	private void finishPreviousInstance(final ClientJob job) {
		if (nonNull(job)) {
			myServerAgent.manage().finishJobExecution(job, false);
			myServerAgent.manage().updateGUI();
		}
	}
}

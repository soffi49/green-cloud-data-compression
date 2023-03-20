package com.greencloud.application.agents.greenenergy.behaviour.powersupply.handler;

import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.handler.logs.PowerSupplyHandlerLog.MANUAL_POWER_SUPPLY_FINISH_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.prepareManualFinishMessageForServer;
import static com.greencloud.application.utils.JobUtils.calculateExpectedJobEndTime;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED_JOB_STATUSES;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.commons.domain.job.ServerJob;

import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour finishes power supply manually if it has not finished on the right time
 */
public class HandleManualPowerSupplyFinish extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleManualPowerSupplyFinish.class);

	private final ServerJob job;
	private final GreenEnergyAgent myGreenEnergyAgent;

	public HandleManualPowerSupplyFinish(final GreenEnergyAgent agent, final Date endDate, final ServerJob job) {
		super(agent, endDate);

		this.myGreenEnergyAgent = agent;
		this.job = job;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param agent agent that executes the behaviour
	 * @param job   job of interest
	 * @return HandleManualPowerSupplyFinish
	 */
	public static HandleManualPowerSupplyFinish create(final GreenEnergyAgent agent, final ServerJob job) {
		final Date endTime = calculateExpectedJobEndTime(job);
		return new HandleManualPowerSupplyFinish(agent, endTime, job);
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param agent agent that executes the behaviour
	 * @param endTime time when job execution should end
	 * @param job     job of interest
	 * @return HandleManualPowerSupplyFinish
	 */
	public static HandleManualPowerSupplyFinish create(final GreenEnergyAgent agent, final Date endTime,
			final ServerJob job) {
		return new HandleManualPowerSupplyFinish(agent, endTime, job);
	}

	/**
	 * Method verifies if the job execution finished correctly.
	 * If there was no information about job finish the Green Source finishes the power supply manually and sends
	 * the warning to the Server Agent.
	 */
	@Override
	protected void onWake() {
		final boolean isJobPresent = myGreenEnergyAgent.getServerJobs().containsKey(job);

		if (isJobPresent && ACCEPTED_JOB_STATUSES.contains(myGreenEnergyAgent.getServerJobs().get(job))) {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.error(MANUAL_POWER_SUPPLY_FINISH_LOG);

			if (isJobStarted(job, myGreenEnergyAgent.getServerJobs())) {
				myGreenEnergyAgent.manage().incrementJobCounter(mapToJobInstanceId(job), FINISH);
			}
			myGreenEnergyAgent.manage().removeJob(job);
			myGreenEnergyAgent.manage().updateGUI();

			myAgent.send(prepareManualFinishMessageForServer(mapToJobInstanceId(job), job.getServer()));
		}
	}
}

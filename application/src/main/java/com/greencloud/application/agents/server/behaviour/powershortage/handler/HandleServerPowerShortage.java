package com.greencloud.application.agents.server.behaviour.powershortage.handler;

import static com.greencloud.application.agents.server.behaviour.powershortage.handler.logs.PowerShortageServerHandlerLog.POWER_SHORTAGE_HANDLE_JOB_ON_BACKUP_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.handler.logs.PowerShortageServerHandlerLog.POWER_SHORTAGE_HANDLE_JOB_ON_HOLD_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.handler.logs.PowerShortageServerHandlerLog.POWER_SHORTAGE_HANDLE_JOB_ON_HOLD_TEMPORARY_LOG;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.PowerUtils.updateAgentMaximumCapacity;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour updates the server state upon power shortage
 */
public class HandleServerPowerShortage extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleServerPowerShortage.class);

	private final ServerAgent myServerAgent;
	private final List<ClientJob> affectedJobs;
	private final Integer newMaximumCapacity;

	private HandleServerPowerShortage(final ServerAgent myAgent, final Date shortageTime,
			final List<ClientJob> affectedJobs, final Integer newMaximumCapacity) {
		super(myAgent, shortageTime);

		this.myServerAgent = myAgent;
		this.affectedJobs = affectedJobs;
		this.newMaximumCapacity = newMaximumCapacity;
	}

	/**
	 * Method creates the behaviour based on the passed parameters
	 *
	 * @param serverAgent     agent executing the behaviour
	 * @param affectedJobs    list of the jobs affected by power shortage
	 * @param shortageStart   time when the power shortage starts
	 * @param newMaximumPower maximum power value during power shortage
	 * @return behaviour scheduling the power shortage handling
	 */
	public static HandleServerPowerShortage createFor(final List<ClientJob> affectedJobs, final Instant shortageStart,
			final ServerAgent serverAgent, final Integer newMaximumPower) {
		final Instant startTime = getCurrentTime().isAfter(shortageStart) ? getCurrentTime() : shortageStart;
		return new HandleServerPowerShortage(serverAgent, Date.from(startTime), affectedJobs,
				newMaximumPower);
	}

	/**
	 * Method is responsible for logging the information about the job status during power shortage.
	 * It updates also the maximum capacity of given server during the power shortage.
	 **/
	@Override
	protected void onWake() {
		affectedJobs.forEach(job -> {
			if (myServerAgent.getServerJobs().containsKey(job)) {
				final JobExecutionStatusEnum jobStatus = myServerAgent.getServerJobs().get(job);
				final String jobId = job.getJobId();

				MDC.put(MDC_JOB_ID, jobId);
				switch (jobStatus) {
					case ON_HOLD_TRANSFER, ON_HOLD_TRANSFER_PLANNED ->
							logger.info(POWER_SHORTAGE_HANDLE_JOB_ON_HOLD_TEMPORARY_LOG, jobId);
					case IN_PROGRESS_BACKUP_ENERGY_PLANNED, IN_PROGRESS_BACKUP_ENERGY ->
							logger.info(POWER_SHORTAGE_HANDLE_JOB_ON_BACKUP_LOG, jobId);
					default -> logger.info(POWER_SHORTAGE_HANDLE_JOB_ON_HOLD_LOG, jobId);
				}
				myServerAgent.manage().updateGUI();
			}
		});
		if (nonNull(newMaximumCapacity)) {
			updateAgentMaximumCapacity(newMaximumCapacity, myServerAgent);
		}
	}
}

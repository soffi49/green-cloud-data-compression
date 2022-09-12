package agents.greenenergy.behaviour.powersupply.handler;

import static agents.greenenergy.behaviour.powersupply.handler.logs.PowerSupplyHandlerLog.MANUAL_POWER_SUPPLY_FINISH_LOG;
import static domain.job.JobStatusEnum.ACCEPTED_JOB_STATUSES;
import static messages.domain.factory.JobStatusMessageFactory.prepareManualFinishMessageForServer;
import static utils.GUIUtils.displayMessageArrow;

import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobInstanceIdentifier;
import domain.job.PowerJob;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour finishes power job manually if it has not finished yet
 */
public class HandleManualPowerSupplyFinish extends WakerBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(HandleManualPowerSupplyFinish.class);

	private final JobInstanceIdentifier jobInstanceId;
	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent         agent which is executing the behaviour
	 * @param endDate       date when the job execution should finish
	 * @param jobInstanceId unique job instance identifier
	 */
	public HandleManualPowerSupplyFinish(final Agent agent, final Date endDate,
			final JobInstanceIdentifier jobInstanceId) {
		super(agent, endDate);
		this.myGreenEnergyAgent = (GreenEnergyAgent) agent;
		this.jobInstanceId = jobInstanceId;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method verifies if the job execution finished correctly.
	 * If there was no information about job finish the Green Source finishes the power supply manually and sends
	 * the warning to the Server Agent.
	 */
	@Override
	protected void onWake() {
		final PowerJob job = myGreenEnergyAgent.manage()
				.getJobByIdAndStartDate(jobInstanceId.getJobId(), jobInstanceId.getStartTime());

		if (Objects.nonNull(job) && ACCEPTED_JOB_STATUSES.contains(myGreenEnergyAgent.getPowerJobs().get(job))) {
			logger.error(MANUAL_POWER_SUPPLY_FINISH_LOG, guid);

			myGreenEnergyAgent.getPowerJobs().remove(job);
			myGreenEnergyAgent.manage().incrementFinishedJobs(job.getJobId());

			displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getOwnerServer());
			myAgent.send(prepareManualFinishMessageForServer(jobInstanceId, myGreenEnergyAgent.getOwnerServer()));
		}
	}
}

package agents.greenenergy.behaviour.powersupply.listener;

import static agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.FINISH_POWER_SUPPLY_LOG;
import static agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.START_POWER_SUPPLY_LOG;
import static agents.greenenergy.behaviour.powersupply.listener.template.PowerSupplyMessageTemplates.POWER_SUPPLY_STATUS_TEMPLATE;
import static domain.job.JobStatusEnum.ACCEPTED;
import static domain.job.JobStatusEnum.IN_PROGRESS;
import static java.util.Objects.nonNull;
import static messages.MessagingUtils.readMessageContent;
import static messages.domain.constants.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static utils.TimeUtils.getCurrentTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobInstanceIdentifier;
import domain.job.PowerJob;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the information that the power supply for given job should start or finish
 */
public class ListenForPowerSupplyStatus extends CyclicBehaviour {
	private static final Logger logger = LoggerFactory.getLogger(ListenForPowerSupplyStatus.class);

	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;

	/**
	 * Behaviour constructor.
	 *
	 * @param myGreenEnergyAgent agent which is executing the behaviour
	 */
	public ListenForPowerSupplyStatus(final GreenEnergyAgent myGreenEnergyAgent) {
		this.myGreenEnergyAgent = myGreenEnergyAgent;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method which listens for the information that the job execution has started/finished. It is responsible
	 * for updating the current green energy source state.
	 */
	@Override
	public void action() {
		final ACLMessage message = myGreenEnergyAgent.receive(POWER_SUPPLY_STATUS_TEMPLATE);
		if (nonNull(message)) {
			final JobInstanceIdentifier jobInstanceId = readMessageContent(message, JobInstanceIdentifier.class);
			final PowerJob powerJob = myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobInstanceId);

			if (nonNull(powerJob)) {
				switch (message.getProtocol()) {
					case FINISH_JOB_PROTOCOL -> handlePowerSupplyFinish(powerJob, jobInstanceId);
					case STARTED_JOB_PROTOCOL -> handlePowerSupplyStart(powerJob, jobInstanceId);
				}
			}
		} else {
			block();
		}
	}

	private void handlePowerSupplyStart(final PowerJob powerJob, final JobInstanceIdentifier jobInstance) {
		logger.info(START_POWER_SUPPLY_LOG, guid, jobInstance.getJobId());
		myGreenEnergyAgent.getPowerJobs().replace(powerJob, ACCEPTED, IN_PROGRESS);
		myGreenEnergyAgent.manage().incrementStartedJobs(jobInstance.getJobId());
	}

	private void handlePowerSupplyFinish(final PowerJob powerJob, final JobInstanceIdentifier jobInstance) {
		logger.info(FINISH_POWER_SUPPLY_LOG, guid, jobInstance.getJobId());
		myGreenEnergyAgent.getPowerJobs().remove(powerJob);
		if (powerJob.getStartTime().isBefore(getCurrentTime())) {
			myGreenEnergyAgent.manage().incrementFinishedJobs(jobInstance.getJobId());
		}
	}
}

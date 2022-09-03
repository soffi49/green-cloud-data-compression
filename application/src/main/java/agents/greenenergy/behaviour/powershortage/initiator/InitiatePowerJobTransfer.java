package agents.greenenergy.behaviour.powershortage.initiator;

import static agents.greenenergy.behaviour.powershortage.initiator.logs.PowerShortageSourceInitiatorLog.SOURCE_JOB_TRANSFER_FAILURE_LOG;
import static agents.greenenergy.behaviour.powershortage.initiator.logs.PowerShortageSourceInitiatorLog.SOURCE_JOB_TRANSFER_FAILURE_NOT_FOUND_LOG;
import static agents.greenenergy.behaviour.powershortage.initiator.logs.PowerShortageSourceInitiatorLog.SOURCE_JOB_TRANSFER_PROCESSING_LOG;
import static agents.greenenergy.behaviour.powershortage.initiator.logs.PowerShortageSourceInitiatorLog.SOURCE_JOB_TRANSFER_REFUSE_NOT_FOUND_LOG;
import static agents.greenenergy.behaviour.powershortage.initiator.logs.PowerShortageSourceInitiatorLog.SOURCE_JOB_TRANSFER_SUCCESSFUL_LOG;
import static agents.greenenergy.behaviour.powershortage.initiator.logs.PowerShortageSourceInitiatorLog.SOURCE_JOB_TRANSFER_SUCCESSFUL_NOT_FOUND_LOG;
import static utils.TimeUtils.getCurrentTime;
import static messages.domain.constants.powershortage.PowerShortageMessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Behaviour initiates the transfer of power jobs affected by the power shortage
 */
public class InitiatePowerJobTransfer extends AchieveREInitiator {

	private static final Logger logger = LoggerFactory.getLogger(InitiatePowerJobTransfer.class);

	private final GreenEnergyAgent myGreenAgent;
	private final String guid;
	private final PowerJob jobToTransfer;
	private final OffsetDateTime powerShortageStart;
	private final ACLMessage transferRequest;

	/**
	 * Behaviours constructor
	 *
	 * @param agent              green source executing the behaviour
	 * @param transferRequest    message with request for the power job transfer
	 * @param jobToTransfer      job that is requested to be transferred
	 * @param powerShortageStart time when the power shortage starts
	 */
	public InitiatePowerJobTransfer(GreenEnergyAgent agent,
			ACLMessage transferRequest,
			PowerJob jobToTransfer,
			OffsetDateTime powerShortageStart) {
		super(agent, transferRequest);
		this.myGreenAgent = agent;
		this.jobToTransfer = jobToTransfer;
		this.guid = myGreenAgent.getLocalName();
		this.powerShortageStart = powerShortageStart;
		this.transferRequest = transferRequest;
	}

	/**
	 * Method handles the AGREE response retrieved from Server informing that the job transfer will
	 * be processed.
	 *
	 * @param agree retrieved response
	 */
	@Override
	protected void handleAgree(ACLMessage agree) {
		logger.info(SOURCE_JOB_TRANSFER_PROCESSING_LOG, guid, myGreenAgent.getOwnerServer().getLocalName(),
				jobToTransfer.getJobId());
	}

	/**
	 * Method handles the REFUSE response retrieved from Server informing that the job transfer
	 * won't be processed.
	 *
	 * @param refuse retrieved response
	 */
	@Override
	protected void handleRefuse(ACLMessage refuse) {
		if (refuse.getContent().equals(JOB_NOT_FOUND_CAUSE_MESSAGE)) {
			logger.info(SOURCE_JOB_TRANSFER_REFUSE_NOT_FOUND_LOG, guid, jobToTransfer.getJobId());
			if (myGreenAgent.getPowerJobs().containsKey(jobToTransfer)) {
				finishNonExistingJob();
			}
		}
	}

	/**
	 * Method handles the INFORM response retrieved from Server informing that the job transfer was established
	 * successfully.
	 * It finished the job instance that will be executed by another server.
	 *
	 * @param inform retrieved response
	 */
	@Override
	protected void handleInform(ACLMessage inform) {
		if (myGreenAgent.getPowerJobs().containsKey(jobToTransfer)) {
			final String jobId = jobToTransfer.getJobId();
			logger.info(SOURCE_JOB_TRANSFER_SUCCESSFUL_LOG, guid, jobId);

			myGreenAgent.getPowerJobs().remove(jobToTransfer);
			if (jobToTransfer.getStartTime().isBefore(getCurrentTime())) {
				myGreenAgent.manage().incrementFinishedJobs(jobToTransfer.getJobId());
			}
		} else {
			logger.info(SOURCE_JOB_TRANSFER_SUCCESSFUL_NOT_FOUND_LOG, guid, jobToTransfer.getJobId());
		}
	}

	/**
	 * Method handles the FAILURE response retrieved from Server informing that the job transfer was established
	 * unsuccessfully.
	 * It then updates the state of the given job to ON_HOLD
	 *
	 * @param failure retrieved response
	 */
	@Override
	protected void handleFailure(ACLMessage failure) {
		final String cause = failure.getContent();
		if (myGreenAgent.getPowerJobs().containsKey(jobToTransfer) &&
				!cause.equals(JOB_NOT_FOUND_CAUSE_MESSAGE)) {
			logger.info(SOURCE_JOB_TRANSFER_FAILURE_LOG, guid, jobToTransfer.getJobId());
			myGreenAgent.getPowerJobs().replace(jobToTransfer, JobStatusEnum.ON_HOLD);
			myGreenAgent.manage().updateGreenSourceGUI();
		} else if (cause.equals(JOB_NOT_FOUND_CAUSE_MESSAGE)) {
			finishNonExistingJob();
		} else {
			logger.info(SOURCE_JOB_TRANSFER_FAILURE_NOT_FOUND_LOG, guid, jobToTransfer.getJobId());
		}
	}

	private void finishNonExistingJob() {
		myGreenAgent.getPowerJobs().remove(jobToTransfer);
		if (jobToTransfer.getStartTime().isBefore(getCurrentTime())) {
			myGreenAgent.manage().incrementFinishedJobs(jobToTransfer.getJobId());
		}
	}
}

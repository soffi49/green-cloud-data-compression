package com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener;

import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.FINISH_POWER_SUPPLY_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.START_POWER_SUPPLY_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.template.PowerSupplyMessageTemplates.POWER_SUPPLY_STATUS_TEMPLATE;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.FINISH_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.STARTED_JOB_ID;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDateAndServer;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_PLANNED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_TRANSFER;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_TRANSFER_PLANNED;
import static java.util.Objects.nonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.domain.job.ServerJob;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the information that the power supply for given job should start or finish
 */
public class ListenForPowerSupplyStatus extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForPowerSupplyStatus.class);

	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param myGreenEnergyAgent agent which is executing the behaviour
	 */
	public ListenForPowerSupplyStatus(final GreenEnergyAgent myGreenEnergyAgent) {
		this.myGreenEnergyAgent = myGreenEnergyAgent;
	}

	/**
	 * Method which listens for the information that the job execution has started/finished. It is responsible
	 * for updating the current green energy source state.
	 */
	@Override
	public void action() {
		final ACLMessage message = myGreenEnergyAgent.receive(POWER_SUPPLY_STATUS_TEMPLATE);
		if (nonNull(message)) {
			final JobStatusUpdate jobStatusUpdate = readMessageContent(message, JobStatusUpdate.class);
			final JobInstanceIdentifier jobInstanceId = jobStatusUpdate.getJobInstance();
			final ServerJob serverJob = getJobByIdAndStartDateAndServer(jobInstanceId, message.getSender(),
					myGreenEnergyAgent.getServerJobs());

			if (nonNull(serverJob)) {
				switch (message.getConversationId()) {
					case FINISH_JOB_ID -> handlePowerSupplyFinish(serverJob, jobInstanceId);
					case STARTED_JOB_ID -> handlePowerSupplyStart(serverJob, jobInstanceId);
				}
			}
		} else {
			block();
		}
	}

	private void handlePowerSupplyStart(final ServerJob serverJob, final JobInstanceIdentifier jobInstance) {
		MDC.put(MDC_JOB_ID, serverJob.getJobId());
		logger.info(START_POWER_SUPPLY_LOG, jobInstance.getJobId());
		myGreenEnergyAgent.getServerJobs().replace(serverJob, ACCEPTED, IN_PROGRESS);
		myGreenEnergyAgent.getServerJobs().replace(serverJob, ON_HOLD_PLANNED, ON_HOLD);
		myGreenEnergyAgent.getServerJobs().replace(serverJob, ON_HOLD_TRANSFER_PLANNED, ON_HOLD_TRANSFER);
		myGreenEnergyAgent.manage().incrementJobCounter(jobInstance, STARTED);
	}

	private void handlePowerSupplyFinish(final ServerJob serverJob, final JobInstanceIdentifier jobInstance) {
		if (isJobStarted(serverJob, myGreenEnergyAgent.getServerJobs())) {
			myGreenEnergyAgent.manage().incrementJobCounter(jobInstance, FINISH);
		}
		MDC.put(MDC_JOB_ID, serverJob.getJobId());
		logger.info(FINISH_POWER_SUPPLY_LOG, jobInstance);
		myGreenEnergyAgent.manage().removeJob(serverJob);
		myGreenEnergyAgent.manage().updateGreenSourceGUI();
	}
}

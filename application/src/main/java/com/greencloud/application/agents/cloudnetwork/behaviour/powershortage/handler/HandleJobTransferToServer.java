package com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.handler;

import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.handler.logs.PowerShortageCloudHandlerLog.SERVER_TRANSFER_EXECUTE_TRANSFER_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.JobUtils.getJobById;
import static com.greencloud.application.utils.TimeUtils.alignStartTimeToCurrentTime;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour updates internal Cloud Network state after performing the job transfer
 */
public class HandleJobTransferToServer extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleJobTransferToServer.class);

	private final CloudNetworkAgent myCloudNetworkAgent;
	private final String jobId;
	private final AID newServer;

	private HandleJobTransferToServer(Agent myAgent, Date transferTime, String jobId, AID newServer) {
		super(myAgent, transferTime);
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
		this.jobId = jobId;
		this.newServer = newServer;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param cloudNetworkAgent cloud network executing the behaviour
	 * @param powerShortageJob  job to be transferred
	 * @param newServer         server which will take over the job execution
	 * @return HandleJobTransferToServer
	 */
	public static HandleJobTransferToServer createFor(final CloudNetworkAgent cloudNetworkAgent,
			final JobPowerShortageTransfer powerShortageJob, final AID newServer) {
		final Date transferTime = Date.from(alignStartTimeToCurrentTime(powerShortageJob.getPowerShortageStart()));
		return new HandleJobTransferToServer(cloudNetworkAgent, transferTime,
				powerShortageJob.getJobInstanceId().getJobId(), newServer);
	}

	/**
	 * Method transfers the job between Servers.
	 * It updates the internal state of the Cloud Network
	 */
	@Override
	protected void onWake() {
		final ClientJob jobToExecute = getJobById(jobId, myCloudNetworkAgent.getNetworkJobs());

		if (nonNull(jobToExecute)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.info(SERVER_TRANSFER_EXECUTE_TRANSFER_LOG, jobId, newServer.getLocalName());
			myCloudNetworkAgent.getServerForJobMap().replace(jobId, newServer);
		}
	}
}

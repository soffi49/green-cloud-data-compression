package agents.cloudnetwork.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.BACK_UP_POWER_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.CallForProposalMessageFactory.createCallForProposal;
import static messages.domain.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.behaviour.powershortage.announcer.AnnounceJobTransferRequest;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Behaviour is responsible for receiving the request from the server which has the power shortage and needs to perform the job transfer
 */
public class ListenForServerPowerShortage extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForServerPowerShortage.class);
	private static final MessageTemplate messageTemplate = and(MatchPerformative(REQUEST),
			MatchProtocol(POWER_SHORTAGE_ALERT_PROTOCOL));

	private CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Cloud Network Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * Method listens for the messages coming from the Server informing that it has some power shortage at the given time.
	 * It handles the information by announcing the job transfer request in network and looking for another server which may execute the job
	 */
	@Override
	public void action() {
		final ACLMessage transferRequest = myAgent.receive(messageTemplate);
		if (Objects.nonNull(transferRequest)) {
			try {
				final PowerShortageJob powerShortageJob = getMapper().readValue(transferRequest.getContent(),
						PowerShortageJob.class);
				final List<AID> remainingServers = getRemainingServers(transferRequest.getSender());
				final Job job = myCloudNetworkAgent.manage().getJobById(powerShortageJob.getJobInstanceId().getJobId());
				myCloudNetworkAgent.send(
						prepareReply(transferRequest.createReply(), powerShortageJob.getJobInstanceId(),
								ACLMessage.AGREE));
				if (Objects.nonNull(job)) {
					final OffsetDateTime startTime = job.getStartTime()
							.isAfter(powerShortageJob.getPowerShortageStart()) ?
							job.getStartTime() :
							powerShortageJob.getPowerShortageStart();
					final Job jobToTransfer = JobMapper.mapToJob(job, startTime);
					final PowerShortageJob newPowerShortageJob = JobMapper.mapToPowerShortageJob(jobToTransfer,
							powerShortageJob.getPowerShortageStart());
					if (!remainingServers.isEmpty()) {
						logger.info("[{}] Sending call for proposal to Server Agents to transfer job with id {}",
								myAgent.getName(), job.getJobId());
						final ACLMessage cfp = createCallForProposal(jobToTransfer, remainingServers,
								CNA_JOB_CFP_PROTOCOL);
						displayMessageArrow(myCloudNetworkAgent, remainingServers);
						myAgent.addBehaviour(
								new AnnounceJobTransferRequest(myAgent, cfp, transferRequest, newPowerShortageJob,
										job.getClientIdentifier()));
					} else {
						logger.info("[{}] No servers available. Passing the information to client and server",
								myAgent.getName());
						displayMessageArrow(myCloudNetworkAgent, new AID(job.getClientIdentifier(), AID.ISGUID));
						myCloudNetworkAgent.send(prepareJobStatusMessageForClient(job.getClientIdentifier(),
								BACK_UP_POWER_JOB_PROTOCOL));
						myCloudNetworkAgent.send(
								prepareReply(transferRequest.createReply(), newPowerShortageJob.getJobInstanceId(),
										ACLMessage.FAILURE));
					}
				} else {
					logger.info("[{}] Job {} for transfer was not found in cloud network", myAgent.getName(),
							powerShortageJob.getJobInstanceId().getJobId());
					myCloudNetworkAgent.send(
							prepareReply(transferRequest.createReply(), powerShortageJob.getJobInstanceId(),
									ACLMessage.FAILURE));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

	private List<AID> getRemainingServers(final AID serverSender) {
		return myCloudNetworkAgent.getOwnedServers().stream().filter(server -> !server.equals(serverSender)).toList();
	}
}

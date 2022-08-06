package agents.server.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageTransferRequest;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.announcer.AnnounceSourceJobTransfer;
import agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import agents.server.behaviour.powershortage.transfer.RequestJobTransferInCloudNetwork;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import messages.domain.CallForProposalMessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Behaviour is responsible for catching the information that the specific green source will have the power shortage at the given time
 */
public class ListenForSourcePowerShortage extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForSourcePowerShortage.class);
	private static final MessageTemplate messageTemplate = and(MatchPerformative(REQUEST),
			MatchProtocol(POWER_SHORTAGE_ALERT_PROTOCOL));

	private ServerAgent myServerAgent;

	/**
	 * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method listens for the messages coming from the Green Source informing about the detected power shortage.
	 * Then, it sends the call for proposal for the job that needs to be transferred to all other green sources. If there are no
	 * available green sources, it passes the transfer request to the parent cloud network
	 */
	@Override
	public void action() {
		final ACLMessage transferRequest = myAgent.receive(messageTemplate);
		if (Objects.nonNull(transferRequest)) {
			try {
				final PowerShortageJob oldJobInstance = getMapper().readValue(transferRequest.getContent(),
						PowerShortageJob.class);
				final String jobId = oldJobInstance.getJobInstanceId().getJobId();
				final PowerJob powerJob = createPowerJobTransferInstance(oldJobInstance);
				final PowerShortageJob jobToTransfer = JobMapper.mapToPowerShortageJob(powerJob,
						oldJobInstance.getPowerShortageStart());
				final List<AID> remainingGreenSources = getRemainingGreenSources(transferRequest.getSender());
				myAgent.send(prepareReply(transferRequest.createReply(), oldJobInstance.getJobInstanceId(),
						ACLMessage.AGREE));
				if (!remainingGreenSources.isEmpty()) {
					logger.info("[{}] Sending call for proposal to Green Source Agents to transfer job with id {}",
							myAgent.getName(), jobId);
					final ACLMessage cfp = CallForProposalMessageFactory.createCallForProposal(powerJob,
							remainingGreenSources, SERVER_JOB_CFP_PROTOCOL);
					displayMessageArrow(myServerAgent, remainingGreenSources);
					myAgent.addBehaviour(new AnnounceSourceJobTransfer(myAgent, cfp, transferRequest, powerJob,
							oldJobInstance.getPowerShortageStart()));
				} else {
					logger.info("[{}] No green sources available. Sending transfer request to cloud network",
							myAgent.getName());
					final ACLMessage transferMessage = preparePowerShortageTransferRequest(oldJobInstance,
							myServerAgent.getOwnerCloudNetworkAgent());
					displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
					myServerAgent.addBehaviour(
							new RequestJobTransferInCloudNetwork(myServerAgent, transferMessage, transferRequest,
									jobToTransfer, false));
				}
				schedulePowerShortageHandling(oldJobInstance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

	private void schedulePowerShortageHandling(final PowerShortageJob jobTransfer) {
		logger.info("[{}] Scheduling power shortage handling", myAgent.getName());
		final Job job = myServerAgent.manage().getJobByIdAndStartDate(jobTransfer.getJobInstanceId());
		myServerAgent.manage().divideJobForPowerShortage(job, jobTransfer.getPowerShortageStart());
		myServerAgent.addBehaviour(
				HandleServerPowerShortage.createFor(Collections.singletonList(job), jobTransfer.getPowerShortageStart(),
						myServerAgent, null));
	}

	private PowerJob createPowerJobTransferInstance(final PowerShortageJob jobTransfer) {
		final Job job = myServerAgent.manage().getJobByIdAndStartDate(jobTransfer.getJobInstanceId());
		final OffsetDateTime startTime = job.getStartTime().isAfter(jobTransfer.getPowerShortageStart()) ?
				job.getStartTime() :
				jobTransfer.getPowerShortageStart();
		return JobMapper.mapToPowerJob(job, startTime);
	}

	private List<AID> getRemainingGreenSources(final AID greenSourceSender) {
		return myServerAgent.getOwnedGreenSources().stream()
				.filter(greenSource -> !greenSource.equals(greenSourceSender)).toList();
	}
}

package agents.server.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageFinishInformation;

import agents.server.ServerAgent;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Behaviour is responsible for listening for the information that the power shortage
 * in the given green source has finished
 */
public class ListenForSourcePowerShortageFinish extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForSourcePowerShortageFinish.class);
	private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM),
			MatchProtocol(POWER_SHORTAGE_FINISH_ALERT_PROTOCOL));

	private ServerAgent myServerAgent;

	/**
	 * Method runs at the start of the behaviour.
	 * It casts the abstract agent to agent of type Server Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method listens for the message coming from the Green Source agent informing that the power
	 * shortage has finished and that the power jobs which were on hold can now be supplied with the
	 * green source power.
	 */
	@Override
	public void action() {
		final ACLMessage inform = myAgent.receive(messageTemplate);

		if (Objects.nonNull(inform)) {
			try {
				logger.info(
						"[{}] Received the information that the power shortage is finished. Using green energy to power up the job",
						myAgent.getName());
				final JobInstanceIdentifier jobInstanceIdentifier = getMapper().readValue(inform.getContent(),
						JobInstanceIdentifier.class);
				final Job job = myServerAgent.manage().getJobByIdAndStartDate(jobInstanceIdentifier);
				final EnumSet<JobStatusEnum> powerShortageStatuses = EnumSet.of(JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY,
						JobStatusEnum.ON_HOLD_SOURCE_SHORTAGE);
				if (Objects.nonNull(job) && powerShortageStatuses.contains(myServerAgent.getServerJobs().get(job))) {
					logger.info("[{}] Supplying job {} with green energy", myAgent.getName(), job.getJobId());
					final JobStatusEnum newStatus = job.getStartTime().isAfter(getCurrentTime()) ?
							JobStatusEnum.ACCEPTED :
							JobStatusEnum.IN_PROGRESS;
					myServerAgent.getServerJobs().replace(job, newStatus);
					myServerAgent.manage().updateServerGUI();
					logger.info(
							"[{}] Passing information to CNA that job {} is being supplied again using the green energy",
							myAgent.getName(), job.getJobId());
					displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
					myServerAgent.send(preparePowerShortageFinishInformation(JobMapper.mapToJobInstanceId(job),
							myServerAgent.getOwnerCloudNetworkAgent()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}
}

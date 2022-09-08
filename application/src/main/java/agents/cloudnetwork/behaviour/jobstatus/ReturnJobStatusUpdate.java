package agents.cloudnetwork.behaviour.jobstatus;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.GREEN_POWER_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.FAILED_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.GREEN_POWER_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static domain.job.JobStatusEnum.IN_PROGRESS;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareJobFailureMessageForClient;
import static messages.domain.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static messages.domain.factory.JobStatusMessageFactory.prepareJobStatusMessageForClient;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for returning to the client job status update
 */
public class ReturnJobStatusUpdate extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ReturnJobStatusUpdate.class);
	private static final MessageTemplate messageTemplate = and(
			or(MatchPerformative(INFORM), MatchPerformative(FAILURE)),
			or(or(or(MatchProtocol(FINISH_JOB_PROTOCOL),
					MatchProtocol(STARTED_JOB_PROTOCOL)),
					MatchProtocol(POWER_SHORTAGE_FINISH_ALERT_PROTOCOL)),
					MatchProtocol(FAILED_JOB_PROTOCOL)));

	private CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Method runs at the behaviour start. It casts the abstract agent to the agent of type CloudNetworkAgent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * Method which listens for the information regarding new job status.
	 * It passes that information to the client.
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(messageTemplate);

		if (Objects.nonNull(message)) {
			try {
				final JobInstanceIdentifier jobInstanceId = getMapper().readValue(message.getContent(),
						JobInstanceIdentifier.class);
				if (Objects.nonNull(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()))) {
					switch (message.getProtocol()) {
						case FINISH_JOB_PROTOCOL -> handleFinishJobMessage(jobInstanceId);
						case STARTED_JOB_PROTOCOL -> handleStartedJobMessage(jobInstanceId);
						case POWER_SHORTAGE_FINISH_ALERT_PROTOCOL -> handleGreenPowerJobMessage(jobInstanceId);
						case FAILED_JOB_PROTOCOL -> handleFailedJobMessage(jobInstanceId);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

	private void handleGreenPowerJobMessage(final JobInstanceIdentifier jobInstanceId) {
		final Job job = myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId());
		logger.info("[{}] Sending information that the job {} is executed again using green power", myAgent.getName(),
				jobInstanceId.getJobId());
		myAgent.send(prepareJobStatusMessageForClient(job.getClientIdentifier(), GREEN_POWER_JOB_PROTOCOL));
	}

	private void handleStartedJobMessage(final JobInstanceIdentifier jobInstanceId) {
		final Job job = myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId());
		if (!myCloudNetworkAgent.getNetworkJobs().get(job).equals(IN_PROGRESS)) {
			logger.info("[{}] Sending information that the job {} execution has started", myAgent.getName(),
					jobInstanceId.getJobId());
			myCloudNetworkAgent.getNetworkJobs()
					.replace(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()), IN_PROGRESS);
			myCloudNetworkAgent.manage().incrementStartedJobs(jobInstanceId.getJobId());
			myAgent.send(prepareJobStatusMessageForClient(job.getClientIdentifier(), STARTED_JOB_PROTOCOL));
		}
	}

	private void handleFinishJobMessage(final JobInstanceIdentifier jobInstanceId) {
		final Long completedJobs = myCloudNetworkAgent.completedJob();
		logger.info("[{}] Sending information that the job {} execution is finished. So far completed {} jobs!",
				myAgent.getName(), jobInstanceId.getJobId(), completedJobs);
		final String clientId = myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()).getClientIdentifier();
		updateNetworkInformation(jobInstanceId.getJobId());
		myAgent.send(prepareJobStatusMessageForClient(clientId, FINISH_JOB_PROTOCOL));
	}

	private void handleFailedJobMessage(final JobInstanceIdentifier jobInstanceId) {
		logger.info("[{}] Sending information that the job {} execution has failed",
				myAgent.getName(), jobInstanceId.getJobId());
		final String clientId = myCloudNetworkAgent
				.manage()
				.getJobById(jobInstanceId.getJobId())
				.getClientIdentifier();
		myCloudNetworkAgent
				.getNetworkJobs()
				.remove(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()));
		myCloudNetworkAgent
				.getServerForJobMap().remove(jobInstanceId.getJobId());
		myAgent.send(prepareJobFailureMessageForClient(clientId, FAILED_JOB_PROTOCOL));
	}

	private void updateNetworkInformation(final String jobId) {
		myCloudNetworkAgent.getNetworkJobs().remove(myCloudNetworkAgent.manage().getJobById(jobId));
		myCloudNetworkAgent.getServerForJobMap().remove(jobId);
		myCloudNetworkAgent.manage().incrementFinishedJobs(jobId);
	}
}

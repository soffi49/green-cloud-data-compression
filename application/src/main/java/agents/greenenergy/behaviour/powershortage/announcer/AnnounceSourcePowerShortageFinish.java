package agents.greenenergy.behaviour.powershortage.announcer;

import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.ON_HOLD_JOB_CHECK_PROTOCOL;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powercheck.ReceiveForecastData;
import agents.greenenergy.behaviour.powercheck.RequestForecastData;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Behaviour is responsible for announcing that the power shortage has finished at the given moment
 */
public class AnnounceSourcePowerShortageFinish extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(AnnounceSourcePowerShortageFinish.class);
	private final GreenEnergyAgent myGreenAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent agent executing the behaviour
	 */
	public AnnounceSourcePowerShortageFinish(GreenEnergyAgent myAgent) {
		super(myAgent);
		this.myGreenAgent = myAgent;
	}

	/**
	 * Method which is responsible for sending the information that the power shortage has
	 * finished and that the jobs which were kept on hold can now be supplied with green power
	 */
	@Override
	public void action() {
		logger.info("[{}] !!!!! Power shortage has finished! Supplying jobs with green power ", myGreenAgent.getName());
		final List<PowerJob> jobsOnHold = getJobsOnHold();
		if (jobsOnHold.isEmpty()) {
			logger.info("[{}] There are no jobs which were on hold. Updating the maximum power",
					myGreenAgent.getName());
		} else {
			logger.info("[{}] Changing the statuses of the jobs and informing the Server Agent",
					myGreenAgent.getLocalName());
			jobsOnHold.forEach(powerJob -> {
				if (myGreenAgent.getPowerJobs().containsKey(powerJob)) {
					logger.info("[{}] Checking if the job {} can be put in progress", myGreenAgent.getLocalName(),
							powerJob.getJobId());
					final String conversationId = String.join("_", powerJob.getJobId(),
							powerJob.getStartTime().toString(), myGreenAgent.getLocalName());
					final SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
					sequentialBehaviour.addSubBehaviour(
							new RequestForecastData(myGreenAgent, conversationId, ON_HOLD_JOB_CHECK_PROTOCOL,
									powerJob));
					sequentialBehaviour.addSubBehaviour(
							new ReceiveForecastData(myGreenAgent, powerJob, ON_HOLD_JOB_CHECK_PROTOCOL, conversationId,
									sequentialBehaviour));
					myAgent.addBehaviour(sequentialBehaviour);
				} else {
					logger.info("[{}] Job {} has ended before supplying it back with green power",
							myGreenAgent.getLocalName(), powerJob.getJobId());
				}
			});
		}
		myGreenAgent.setMaximumCapacity(myGreenAgent.getInitialMaximumCapacity());
	}

	private List<PowerJob> getJobsOnHold() {
		return myGreenAgent.getPowerJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(JobStatusEnum.ON_HOLD) && job.getKey().getEndTime()
						.isAfter(getCurrentTime()))
				.map(Map.Entry::getKey)
				.toList();
	}
}

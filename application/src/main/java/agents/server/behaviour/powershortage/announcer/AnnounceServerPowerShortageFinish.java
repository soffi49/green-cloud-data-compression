package agents.server.behaviour.powershortage.announcer;

import static common.TimeUtils.getCurrentTime;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageFinishInformation;

import agents.server.ServerAgent;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Behaviour is responsible for passing the information that the power shortage for given server has finished
 */
public class AnnounceServerPowerShortageFinish extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(AnnounceServerPowerShortageFinish.class);
	private final ServerAgent myServerAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent agent executing the behaviour
	 */
	public AnnounceServerPowerShortageFinish(ServerAgent myAgent) {
		super(myAgent);
		this.myServerAgent = myAgent;
	}

	/**
	 * Method which is responsible for passing the information that the power shortage has
	 * finished and that the jobs which were supplied using the back-up power can now use again the
	 * green energy power
	 */
	@Override
	public void action() {
		logger.info("[{}] !!!!! Power shortage has finished! Supplying jobs with green power ",
				myServerAgent.getName());
		final List<Job> jobsOnHold = getJobsOnHold();
		myServerAgent.setCurrentMaximumCapacity(myServerAgent.getInitialMaximumCapacity());
		if (jobsOnHold.isEmpty()) {
			logger.info("[{}] There are no jobs supplied using back up power. Updating the maximum power",
					myServerAgent.getName());
		} else {
			logger.info("[{}] Changing the statuses of the jobs and informing the CNA and Green Sources",
					myServerAgent.getLocalName());
			jobsOnHold.forEach(job -> {
				if (myServerAgent.getServerJobs().containsKey(job) && myServerAgent.getGreenSourceForJobMap()
						.containsKey(job.getJobId())) {
					if (myServerAgent.manage().getAvailableCapacity(job.getStartTime(), job.getEndTime(),
							JobMapper.mapToJobInstanceId(job)) < job.getPower()) {
						logger.info(
								"[{}] There is not enough available power to bring job to in progress! Checking for back up power",
								myServerAgent.getLocalName());
						if (myServerAgent.manage().getBackUpAvailableCapacity(job.getStartTime(), job.getEndTime(),
								JobMapper.mapToJobInstanceId(job)) < job.getPower()) {
							logger.info(
									"[{}] There is not enough available power to supply job with back up power! Leaving job on hold",
									myServerAgent.getLocalName());
						} else {
							logger.info("[{}] Supporting job with back-up power!", myServerAgent.getLocalName());
							myServerAgent.getServerJobs().replace(job, JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY);
							myServerAgent.manage().updateServerGUI();
						}
					} else {
						logger.info("[{}] Changing the status of the job {}", myServerAgent.getLocalName(),
								job.getJobId());
						final JobStatusEnum newStatus = job.getStartTime().isAfter(getCurrentTime()) ?
								JobStatusEnum.ACCEPTED :
								JobStatusEnum.IN_PROGRESS;
						final AID greenSource = myServerAgent.getGreenSourceForJobMap().get(job.getJobId());
						myServerAgent.getServerJobs().replace(job, newStatus);
						myServerAgent.manage().updateServerGUI();
						myServerAgent.send(preparePowerShortageFinishInformation(JobMapper.mapToJobInstanceId(job),
								myServerAgent.getOwnerCloudNetworkAgent()));
						myServerAgent.send(
								preparePowerShortageFinishInformation(JobMapper.mapToJobInstanceId(job), greenSource));
					}
				}
			});
		}
	}

	private List<Job> getJobsOnHold() {
		return myServerAgent.getServerJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(JobStatusEnum.ON_HOLD)
						&& job.getKey().getEndTime().isAfter(getCurrentTime()))
				.map(Map.Entry::getKey)
				.toList();
	}
}

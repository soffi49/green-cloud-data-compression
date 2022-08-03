package agents.greenenergy.behaviour.powershortage.announcer;

import static common.TimeUtils.getCurrentTime;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageFinishInformation;

import agents.greenenergy.GreenEnergyAgent;
import common.mapper.JobMapper;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.behaviours.OneShotBehaviour;

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
					logger.info("[{}] Changing the status of the job {}", myGreenAgent.getLocalName(),
							powerJob.getJobId());
					final JobStatusEnum newStatus = powerJob.getStartTime().isAfter(getCurrentTime()) ?
							JobStatusEnum.ACCEPTED :
							JobStatusEnum.IN_PROGRESS;
					myGreenAgent.getPowerJobs().replace(powerJob, newStatus);
					myGreenAgent.manage().updateGreenSourceGUI();
					myGreenAgent.send(preparePowerShortageFinishInformation(JobMapper.mapToJobInstanceId(powerJob),
							myGreenAgent.getOwnerServer()));
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
				.filter(job -> job.getValue().equals(JobStatusEnum.ON_HOLD)
						&& job.getKey().getEndTime().isAfter(getCurrentTime()))
				.map(Map.Entry::getKey)
				.toList();
	}
}

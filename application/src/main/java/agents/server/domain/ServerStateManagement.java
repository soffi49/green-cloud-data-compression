package agents.server.domain;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.TimeUtils.isWithinTimeStamp;
import static domain.job.JobStatusEnum.ACCEPTED_JOB_STATUSES;
import static domain.job.JobStatusEnum.GREEN_POWER_SERVER_JOB_STATUSES;
import static domain.job.JobStatusEnum.IN_PROGRESS;
import static domain.job.JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static domain.job.JobStatusEnum.JOB_ON_HOLD;
import static domain.job.JobStatusEnum.ON_HOLD_SOURCE_SHORTAGE;
import static java.time.temporal.ChronoUnit.HOURS;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;

import agents.server.ServerAgent;
import agents.server.behaviour.FinishJobExecution;
import agents.server.behaviour.StartJobExecution;

import com.gui.domain.nodes.ServerAgentNode;

import common.TimeUtils;
import common.mapper.JobMapper;
import domain.GreenSourceData;
import domain.job.ImmutableJob;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Set of utilities used to manage the internal state of the server agent
 */
public class ServerStateManagement {

	private static final Logger logger = LoggerFactory.getLogger(ServerStateManagement.class);

	protected final AtomicInteger uniqueStartedJobs;
	protected final AtomicInteger uniqueFinishedJobs;
	protected final AtomicInteger startedJobsInstances;
	protected final AtomicInteger finishedJobsInstances;
	private final ServerAgent serverAgent;

	public ServerStateManagement(ServerAgent serverAgent) {
		this.serverAgent = serverAgent;
		this.uniqueStartedJobs = new AtomicInteger(0);
		this.uniqueFinishedJobs = new AtomicInteger(0);
		this.startedJobsInstances = new AtomicInteger(0);
		this.finishedJobsInstances = new AtomicInteger(0);
	}

	/**
	 * Method computes the available power for given time frame
	 *
	 * @param startDate starting date
	 * @param endDate   end date
	 * @return available power
	 */
	public synchronized int getAvailableCapacity(final OffsetDateTime startDate, final OffsetDateTime endDate) {
		var usedPower = serverAgent.getServerJobs().keySet().stream()
				.filter(job -> TimeUtils.isWithinTimeStampWithBuffer(job.getStartTime(), job.getEndTime(), startDate)
						|| TimeUtils.isWithinTimeStampWithBuffer(job.getStartTime(), job.getEndTime(), endDate))
				.map(Job::getPower).mapToInt(Integer::intValue).sum();
		return serverAgent.getCurrentMaximumCapacity() - usedPower;
	}

	/**
	 * Method computes the available back-up power for given time frame and active jobs
	 *
	 * @param startDate    starting date
	 * @param endDate      end date
	 * @param jobToExclude job to exclude from set
	 * @return available power
	 */
	public synchronized int getBackUpAvailableCapacity(final OffsetDateTime startDate, final OffsetDateTime endDate,
			final JobInstanceIdentifier jobToExclude) {
		var usedPower = serverAgent.getServerJobs().keySet().stream()
				.filter(job -> TimeUtils.isWithinTimeStampWithBuffer(job.getStartTime(), job.getEndTime(), startDate)
						|| TimeUtils.isWithinTimeStampWithBuffer(job.getStartTime(), job.getEndTime(), endDate)
						&& serverAgent.getServerJobs().get(job).equals(IN_PROGRESS_BACKUP_ENERGY)
						&& !JobMapper.mapToJobInstanceId(job).equals(jobToExclude)).map(Job::getPower)
				.mapToInt(Integer::intValue).sum();
		return serverAgent.getInitialMaximumCapacity() - usedPower;
	}

	/**
	 * Method computes the available power for given time frame and active jobs
	 *
	 * @param startDate    starting date
	 * @param endDate      end date
	 * @param jobToExclude job to exclude from set
	 * @return available power
	 */
	public synchronized int getAvailableCapacity(final OffsetDateTime startDate, final OffsetDateTime endDate,
			final JobInstanceIdentifier jobToExclude) {
		var usedPower = serverAgent.getServerJobs().keySet().stream()
				.filter(job -> TimeUtils.isWithinTimeStampWithBuffer(job.getStartTime(), job.getEndTime(), startDate)
						|| TimeUtils.isWithinTimeStampWithBuffer(job.getStartTime(), job.getEndTime(), endDate)
						&& GREEN_POWER_SERVER_JOB_STATUSES.contains(serverAgent.getServerJobs().get(job))
						&& !JobMapper.mapToJobInstanceId(job).equals(jobToExclude)).map(Job::getPower)
				.mapToInt(Integer::intValue).sum();
		return serverAgent.getCurrentMaximumCapacity() - usedPower;
	}

	/**
	 * Method performs default behaviour when the job is finished
	 *
	 * @param jobToFinish job to be finished
	 * @param informCNA   flag indicating whether cloud network should be informed about the job finish
	 */
	public void finishJobExecution(final Job jobToFinish, final boolean informCNA) {
		final JobStatusEnum jobStatusEnum = serverAgent.getServerJobs().get(jobToFinish);
		final List<AID> receivers = informCNA ?
				List.of(serverAgent.getGreenSourceForJobMap().get(jobToFinish.getJobId()),
						serverAgent.getOwnerCloudNetworkAgent()) :
				Collections.singletonList(serverAgent.getGreenSourceForJobMap().get(jobToFinish.getJobId()));
		final ACLMessage finishJobMessage = prepareFinishMessage(jobToFinish.getJobId(), jobToFinish.getStartTime(),
				receivers);
		serverAgent.getServerJobs().remove(jobToFinish);
		if (Objects.isNull(serverAgent.manage().getJobById(jobToFinish.getJobId()))) {
			serverAgent.getGreenSourceForJobMap().remove(jobToFinish.getJobId());
		}
		if (jobStatusEnum.equals(IN_PROGRESS_BACKUP_ENERGY)) {
			serverAgent.getServerJobs().entrySet().stream().filter(job ->
					isWithinTimeStamp(job.getKey().getStartTime(), job.getKey().getEndTime(), getCurrentTime())
							&& job.getValue().equals(ON_HOLD_SOURCE_SHORTAGE)
							&& job.getKey().getPower() <= jobToFinish.getPower()).forEach(job -> {
				if (getBackUpAvailableCapacity(job.getKey().getStartTime(), job.getKey().getEndTime(),
						JobMapper.mapToJobInstanceId(job.getKey())) >= job.getKey().getPower()) {
					logger.info("[{}] Supplying job {} with back up power", serverAgent.getName(),
							job.getKey().getJobId());
					job.setValue(IN_PROGRESS_BACKUP_ENERGY);
					updateServerGUI();
				}
			});
		}
		incrementFinishedJobs(jobToFinish.getJobId());
		displayMessageArrow(serverAgent, receivers);
		serverAgent.send(finishJobMessage);
	}

	/**
	 * Method calculates the price for executing the job by given green source and server
	 *
	 * @param greenSourceData green source executing the job
	 * @return full price
	 */
	public double calculateServicePrice(final GreenSourceData greenSourceData) {
		var job = getJobById(greenSourceData.getJobId());
		var powerCost = job.getPower() * greenSourceData.getPricePerPowerUnit();
		var computingCost = HOURS.between(job.getEndTime(), job.getStartTime()) * serverAgent.getPricePerHour();
		return powerCost + computingCost;
	}

	/**
	 * Method returns the instance of the job for current time
	 *
	 * @param jobId unique job identifier
	 * @return pair of job and current status
	 */
	public Map.Entry<Job, JobStatusEnum> getCurrentJobInstance(final String jobId) {
		final OffsetDateTime currentTime = getCurrentTime();
		return serverAgent.getServerJobs().entrySet().stream().filter(jobEntry -> {
			final Job job = jobEntry.getKey();
			return job.getJobId().equals(jobId) && (
					(job.getStartTime().isBefore(currentTime) && job.getEndTime().isAfter(currentTime))
							|| job.getEndTime().equals(currentTime));
		}).findFirst().orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and star time from job map
	 *
	 * @param jobId     job identifier
	 * @param startTime job start time
	 * @return job
	 */
	public Job getJobByIdAndStartDate(final String jobId, final OffsetDateTime startTime) {
		return serverAgent.getServerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId) && job.getStartTime().isEqual(startTime)).findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job by the job instance id
	 *
	 * @param jobInstanceId job instance identifier
	 * @return job
	 */
	public Job getJobByIdAndStartDate(final JobInstanceIdentifier jobInstanceId) {
		return serverAgent.getServerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobInstanceId.getJobId()) && job.getStartTime()
						.isEqual(jobInstanceId.getStartTime())).findFirst().orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and end time from job map
	 *
	 * @param jobId   job identifier
	 * @param endTime job end time
	 * @return job
	 */
	public Job getJobByIdAndEndDate(final String jobId, final OffsetDateTime endTime) {
		return serverAgent.getServerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId) && job.getEndTime().isEqual(endTime)).findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job based on the given id
	 *
	 * @param jobId unique job identifier
	 * @return Job
	 */
	public Job getJobById(final String jobId) {
		return serverAgent.getServerJobs().keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst()
				.orElse(null);
	}

	/**
	 * Method verifies if there is only 1 instance of the given job
	 *
	 * @param jobId unique job identifier
	 * @return boolean
	 */
	public boolean isJobUnique(final String jobId) {
		return serverAgent.getServerJobs().keySet().stream().filter(job -> job.getJobId().equals(jobId)).toList().size()
				== 1;
	}

	/**
	 * Method increments the count of started jobs
	 *
	 * @param jobId unique job identifier
	 */
	public void incrementStartedJobs(final String jobId) {
		if (isJobUnique(jobId)) {
			uniqueStartedJobs.getAndAdd(1);
			logger.info("[{}] Started job {}. Number of unique started jobs is {}", serverAgent.getLocalName(), jobId,
					uniqueStartedJobs);
		}
		startedJobsInstances.getAndAdd(1);
		logger.info("[{}] Started job instance {}. Number of started job instances is {}", serverAgent.getLocalName(),
				jobId, startedJobsInstances);
		updateServerGUI();
	}

	/**
	 * Method increments the count of finished jobs
	 *
	 * @param jobId unique identifier of the job
	 */
	public void incrementFinishedJobs(final String jobId) {
		if (isJobUnique(jobId)) {
			uniqueFinishedJobs.getAndAdd(1);
			logger.info("[{}] Finished job {}. Number of unique finished jobs is {} out of {} started",
					serverAgent.getLocalName(), jobId, uniqueFinishedJobs, uniqueStartedJobs);
		}
		finishedJobsInstances.getAndAdd(1);
		logger.info("[{}] Finished job instance {}. Number of finished job instances is {} out of {} started",
				serverAgent.getLocalName(), jobId, finishedJobsInstances, startedJobsInstances);
		updateServerGUI();
	}

	/**
	 * Method changes the server's maximum capacity
	 *
	 * @param newMaximumCapacity new maximum capacity value
	 */
	public void updateMaximumCapacity(final int newMaximumCapacity) {
		serverAgent.setCurrentMaximumCapacity(newMaximumCapacity);
		((ServerAgentNode) serverAgent.getAgentNode()).updateMaximumCapacity(serverAgent.getCurrentMaximumCapacity());
	}

	/**
	 * Method creates new instances for given job which will be affected by the power shortage
	 *
	 * @param job                affected job
	 * @param powerShortageStart time when power shortage starts
	 */
	public Job divideJobForPowerShortage(final Job job, final OffsetDateTime powerShortageStart) {
		if (powerShortageStart.isAfter(job.getStartTime()) && !powerShortageStart.equals(job.getStartTime())) {
			final Job onBackupEnergyInstance = ImmutableJob.builder().jobId(job.getJobId())
					.clientIdentifier(job.getClientIdentifier()).power(job.getPower()).startTime(powerShortageStart)
					.endTime(job.getEndTime()).build();
			final Job finishedPowerJobInstance = ImmutableJob.builder().jobId(job.getJobId())
					.clientIdentifier(job.getClientIdentifier()).power(job.getPower()).startTime(job.getStartTime())
					.endTime(powerShortageStart).build();
			final JobStatusEnum currentJobStatus = serverAgent.getServerJobs().get(job);
			serverAgent.getServerJobs().remove(job);
			serverAgent.getServerJobs().put(onBackupEnergyInstance, JobStatusEnum.ON_HOLD_TRANSFER);
			serverAgent.getServerJobs().put(finishedPowerJobInstance, currentJobStatus);
			serverAgent.addBehaviour(StartJobExecution.createFor(serverAgent, onBackupEnergyInstance, false, true));
			if (getCurrentTime().isBefore(finishedPowerJobInstance.getStartTime())) {
				serverAgent.addBehaviour(
						StartJobExecution.createFor(serverAgent, finishedPowerJobInstance, true, false));
			} else {
				serverAgent.addBehaviour(FinishJobExecution.createFor(serverAgent, finishedPowerJobInstance, false));
			}
			return onBackupEnergyInstance;
		} else {
			serverAgent.getServerJobs().replace(job, JobStatusEnum.ON_HOLD_TRANSFER);
			updateServerGUI();
			return job;
		}
	}

	private List<Job> getUniqueJobsForTimeStamp(final OffsetDateTime startDate, final OffsetDateTime endDate) {
		return serverAgent.getServerJobs().keySet().stream()
				.filter(job -> ACCEPTED_JOB_STATUSES.contains(serverAgent.getServerJobs().get(job)))
				.filter(job -> TimeUtils.isWithinTimeStamp(startDate, endDate, job.getStartTime())
						|| TimeUtils.isWithinTimeStamp(startDate, endDate, job.getEndTime())).map(Job::getJobId)
				.collect(Collectors.toSet()).stream().collect(Collectors.toMap(jobId -> jobId, this::getJobById))
				.values().stream().toList();
	}

	/**
	 * Method updates the information on the server GUI
	 */
	public void updateServerGUI() {
		final ServerAgentNode serverAgentNode = (ServerAgentNode) serverAgent.getAgentNode();
		serverAgentNode.updateMaximumCapacity(serverAgent.getCurrentMaximumCapacity());
		serverAgentNode.updateJobsCount(getJobCount());
		serverAgentNode.updateClientNumber(getClientNumber());
		serverAgentNode.updateIsActive(getIsActiveState(), getIsActiveBackUpState());
		serverAgentNode.updateTraffic(getCurrentPowerInUseForServer());
		serverAgentNode.updateBackUpTraffic(getCurrentBackUpPowerInUseForServer());
		serverAgentNode.updateOnHoldJobsCount(getOnHoldJobsCount());
	}

	/**
	 * Method updates the client number
	 */
	public void updateClientNumber() {
		((ServerAgentNode) serverAgent.getAgentNode()).updateClientNumber(getClientNumber());
	}

	private int getJobCount() {
		return serverAgent.getServerJobs().entrySet().stream()
				.filter(job -> ACCEPTED_JOB_STATUSES.contains(job.getValue()) && isWithinTimeStamp(
						job.getKey().getStartTime(), job.getKey().getEndTime(), getCurrentTime()))
				.map(Map.Entry::getKey).map(Job::getJobId).collect(Collectors.toSet()).size();
	}

	private int getClientNumber() {
		return serverAgent.getGreenSourceForJobMap().size();
	}

	private int getCurrentPowerInUseForServer() {
		return serverAgent.getServerJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(IN_PROGRESS) && isWithinTimeStamp(job.getKey().getStartTime(),
						job.getKey().getEndTime(), getCurrentTime())).mapToInt(job -> job.getKey().getPower()).sum();
	}

	private int getCurrentBackUpPowerInUseForServer() {
		return serverAgent.getServerJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(IN_PROGRESS_BACKUP_ENERGY) && isWithinTimeStamp(
						job.getKey().getStartTime(), job.getKey().getEndTime(), getCurrentTime()))
				.mapToInt(job -> job.getKey().getPower()).sum();
	}

	private int getOnHoldJobsCount() {
		return serverAgent.getServerJobs().entrySet().stream()
				.filter(job -> JOB_ON_HOLD.contains(job.getValue()) && isWithinTimeStamp(job.getKey().getStartTime(),
						job.getKey().getEndTime(), getCurrentTime())).toList().size();
	}

	private boolean getIsActiveState() {
		return getCurrentPowerInUseForServer() > 0 || getCurrentBackUpPowerInUseForServer() > 0;
	}

	private boolean getIsActiveBackUpState() {
		return getCurrentBackUpPowerInUseForServer() > 0;
	}
}

package com.greencloud.application.agents;

import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobNewEndTime;
import static com.greencloud.application.mapper.JobMapper.mapToJobNewStartTime;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_HOLD;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_TRANSFER;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_TRANSFER_PLANNED;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.domain.job.JobCounter;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Abstract class inherited by all agent state management services which gathers their common methods and properties
 */
public abstract class AbstractStateManagement extends AbstractAgentManagement {

	private static final Logger logger = getLogger(AbstractStateManagement.class);

	protected final ConcurrentMap<JobExecutionResultEnum, JobCounter> jobCounters;

	protected AbstractStateManagement() {
		this.jobCounters = getJobCountersMap();
	}

	/**
	 * Method used for incrementing the counters of the jobs
	 *
	 * @param jobInstance job identifier
	 * @param type        type of counter to increment
	 */
	public void incrementJobCounter(final JobInstanceIdentifier jobInstance, final JobExecutionResultEnum type) {
		final JobCounter counter = jobCounters.get(type);
		counter.count().getAndIncrement();

		MDC.put(MDC_JOB_ID, jobInstance.getJobId());
		counter.handler().accept(jobInstance);

		updateGUI();
	}

	/**
	 * Generic method used for comparing offers proposed to an agent
	 *
	 * @param offer1 first offer for comparison
	 * @param offer2 second offer for comparison
	 * @param type   type of the offers content
	 * @return method returns:
	 * <p> val > 0 - if the offer1 is better</p>
	 * <p> val = 0 - if both offers are equivalently good</p>
	 * <p> val < 0 - if the offer2 is better</p>
	 */
	public <T> int compareReceivedOffers(final ACLMessage offer1, final ACLMessage offer2,
			final Class<T> type, final Comparator<T> comparator) {
		try {
			final T offer1Content = readMessageContent(offer1, type);
			final T offer2Content = readMessageContent(offer2, type);

			return comparator.compare(offer1Content, offer2Content);
		} catch (IncorrectMessageContentException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * <p/>
	 * Method creates new instances for given job with respect to given job transfer time.
	 * If the transfer time is set after the start of job execution -> job will be divided into 2 instances.
	 * <p/>
	 * Example:
	 * Job1 (start: 08:00, finish: 10:00)
	 * Transfer time: 09:00
	 *
	 * <p> Job1Instance1: (start: 08:00, finish: 09:00) <- job not affected by power shortage </p>
	 * <p> Job1Instance2: (start: 09:00, finish: 10:00) <- job affected by power shortage </p>
	 *
	 * @param job                job that is to be divided into instances
	 * @param powerShortageStart time when the power shortage will start
	 * @param jobMap             map of jobs of interest
	 * @return job instance for transfer
	 */
	public <T extends PowerJob> T divideJobForPowerShortage(final T job, final Instant powerShortageStart,
			final ConcurrentMap<T, JobExecutionStatusEnum> jobMap) {
		if (powerShortageStart.isAfter(job.getStartTime())) {
			final T affectedJobInstance = mapToJobNewStartTime(job, powerShortageStart);
			final T notAffectedJobInstance = mapToJobNewEndTime(job, powerShortageStart);
			final JobExecutionStatusEnum currentJobStatus = jobMap.get(job);

			logger.info("Job before shortage: {} Job after shortage {}", affectedJobInstance, notAffectedJobInstance);

			jobMap.remove(job);
			jobMap.put(affectedJobInstance, ON_HOLD_TRANSFER_PLANNED);
			jobMap.put(notAffectedJobInstance, currentJobStatus);

			processJobDivision(affectedJobInstance, notAffectedJobInstance);
			updateGUI();
			return affectedJobInstance;
		} else {
			jobMap.replace(job, EXECUTING_TRANSFER.getStatus(isJobStarted(job, jobMap)));
			updateGUI();
			return job;
		}
	}

	/**
	 * Method retrieves jobs which execution hasn't finished and their status is on hold
	 *
	 * @param jobMap map of jobs of interest
	 * @return list of jobs on hold
	 */
	public <T extends PowerJob> List<T> getActiveJobsOnHold(final ConcurrentMap<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.entrySet().stream()
				.filter(job -> EXECUTING_ON_HOLD.getStatuses().contains(job.getValue()))
				.filter(job -> job.getKey().getEndTime().isAfter(getCurrentTime()))
				.map(Map.Entry::getKey)
				.toList();
	}

	/**
	 * Method retrieves list of agents excluding particular one
	 *
	 * @param agent           agent to exclude
	 * @param connectedAgents list of connected agents
	 * @return list of remaining agents AIDs
	 */
	public List<AID> getRemainingAgents(final AID agent, final Collection<AID> connectedAgents) {
		return connectedAgents.stream()
				.filter(server -> !server.equals(agent))
				.toList();
	}

	/**
	 * Method that builds job counters map (to be overridden)
	 */
	protected ConcurrentMap<JobExecutionResultEnum, JobCounter> getJobCountersMap() {
		return new ConcurrentHashMap<>();
	}

	/**
	 * Method defines handler that should be executed to complete job division (to be overridden)
	 *
	 * @param affectedJob    job instance affected by power shortage
	 * @param nonAffectedJob job instance not affected by power shortage
	 * @return Runnable handler
	 */
	protected <T extends PowerJob> void processJobDivision(final T affectedJob, final T nonAffectedJob) {
	}

	/**
	 * Method used in updating GUI associated with given agent (to be overridden)
	 */
	public void updateGUI() {
	}
}

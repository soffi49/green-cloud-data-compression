package com.greencloud.application.agents;

import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.MDC;

import com.greencloud.application.domain.job.JobCounter;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;

import jade.lang.acl.ACLMessage;

/**
 * Abstract class inherited by all agent state management services which gathers their common methods and properties
 */
public abstract class AbstractStateManagement extends AbstractAgentManagement {

	protected final ConcurrentMap<JobExecutionResultEnum, JobCounter> jobCounters;

	protected AbstractStateManagement() {
		this.jobCounters = getJobCountersMap();
	}

	/**
	 * Method used for incrementing the counters of the jobs
	 *
	 * @param jobId job identifier
	 * @param type  type of counter to increment
	 */
	public void incrementJobCounter(final String jobId, final JobExecutionResultEnum type) {
		final JobCounter counter = jobCounters.get(type);
		counter.count().getAndIncrement();

		MDC.put(MDC_JOB_ID, jobId);
		counter.handler().accept(jobId);

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
	 * Method that builds job counters map (to be overridden)
	 */
	protected ConcurrentMap<JobExecutionResultEnum, JobCounter> getJobCountersMap() {
		return new ConcurrentHashMap<>();
	}

	/**
	 * Method used in updating GUI associated with given agent (to be overridden)
	 */
	public void updateGUI() {
	}
}

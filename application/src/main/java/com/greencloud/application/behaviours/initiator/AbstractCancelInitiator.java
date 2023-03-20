package com.greencloud.application.behaviours.initiator;

import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.COULD_NOT_CANCEL;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.commons.domain.job.ClientJob;

import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Abstract behaviour that can be inherited in order to perform generic job cancellation
 */
public abstract class AbstractCancelInitiator<T extends ClientJob> extends AchieveREInitiator {

	protected final AbstractAgent myAbstractAgent;
	protected final ACLMessage originalRequest;
	protected final List<T> jobsToCancel;
	protected final List<String> cancelledJobParts;

	/**
	 * Method creates the behaviour
	 *
	 * @param agent           agent executing the behaviour
	 * @param cancel          cancellation message
	 * @param jobsToCancel    list of jobs that are to be cancelled
	 * @param originalRequest original cancellation request
	 */
	protected AbstractCancelInitiator(final AbstractAgent agent, final ACLMessage cancel,
			final List<T> jobsToCancel, final ACLMessage originalRequest) {
		super(agent, cancel);

		this.myAbstractAgent = agent;
		this.jobsToCancel = jobsToCancel;
		this.originalRequest = originalRequest;
		this.cancelledJobParts = new ArrayList<>();
	}

	/**
	 * Method verifies if some job parts are still left and if so then
	 * responds with REFUSE (to indicate that cancellation was not possible)
	 *
	 * @param responses responses retrieved from Servers
	 */
	@Override
	protected void handleAllResponses(final Vector responses) {
		postCancellation();

		if (!jobsToCancel.isEmpty()) {
			myAbstractAgent.send(prepareStringReply(originalRequest, COULD_NOT_CANCEL, REFUSE));
		} else {
			myAbstractAgent.send(prepareReply(originalRequest, cancelledJobParts, INFORM));
		}
	}

	/**
	 * Method handles information from owned agent about job cancellation.
	 * It receives list of IDs of job parts that were cancelled.
	 *
	 * @param inform retrieved message
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void handleInform(final ACLMessage inform) {
		final List<String> cancelledJobs = readMessageContent(inform, (Class<List<String>>) ((Class) List.class));

		cancelledJobs.forEach(cancelledJob -> {
			final T job = jobsToCancel.stream().filter(el -> el.getJobId().equals(cancelledJob))
					.findFirst().orElse(null);

			if (nonNull(job)) {
				handleJobCanceling(job);
				jobsToCancel.remove(job);
				cancelledJobParts.add(job.getJobId());
			}
		});
	}

	/**
	 * Abstract method invoked to process job cancelling
	 *
	 * @param jobToCancel job that is to be cancelled
	 */
	public abstract void handleJobCanceling(final T jobToCancel);

	/**
	 * Abstract method that can be used to invoke some post-cancellation procedure
	 */
	public abstract void postCancellation();
}

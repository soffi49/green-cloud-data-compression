package com.greencloud.application.agents.scheduler.behaviour.job.cancellation;

import com.greencloud.application.agents.scheduler.SchedulerAgent;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour makes another attempt to cancel the remaining job parts
 */
public class RetryJobCancellation extends OneShotBehaviour {

	private final SchedulerAgent mySchedulerAgent;
	private final String originalJobId;

	/**
	 * Behaviour constructor
	 *
	 * @param agent         agent executing the behaviour
	 * @param originalJobId id of the job of interest
	 */
	public RetryJobCancellation(SchedulerAgent agent, String originalJobId) {
		super(agent);

		this.mySchedulerAgent = agent;
		this.originalJobId = originalJobId;
	}

	/**
	 * Method initiates once again the cancellation behaviour
	 */
	@Override
	public void action() {
		mySchedulerAgent.addBehaviour(InitiateJobCancellation.create(mySchedulerAgent, originalJobId));
	}
}

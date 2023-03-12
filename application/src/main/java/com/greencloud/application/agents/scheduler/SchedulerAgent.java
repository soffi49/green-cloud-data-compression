package com.greencloud.application.agents.scheduler;

import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SCHEDULER_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SCHEDULER_SERVICE_TYPE;
import static com.greencloud.commons.utils.CommonUtils.isFibonacci;
import static java.lang.Integer.parseInt;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.scheduler.behaviour.df.SubscribeCloudNetworkService;
import com.greencloud.application.agents.scheduler.behaviour.job.scheduling.handler.HandleJobAnnouncement;
import com.greencloud.application.agents.scheduler.behaviour.job.scheduling.listener.ListenForClientJob;
import com.greencloud.application.agents.scheduler.behaviour.job.scheduling.listener.ListenForJobUpdate;
import com.greencloud.application.agents.scheduler.managment.SchedulerConfigurationManagement;
import com.greencloud.application.agents.scheduler.managment.SchedulerStateManagement;

import jade.core.behaviours.Behaviour;

/**
 * Agent representing the Scheduler that orchestrate the job announcement in Cloud Network
 */
public class SchedulerAgent extends AbstractSchedulerAgent {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerAgent.class);

	@Override
	protected void initializeAgent(final Object[] args) {
		if (Objects.nonNull(args) && args.length == 5) {
			try {
				final int deadlineWeight = parseInt(args[0].toString());
				final int powerWeight = parseInt(args[1].toString());
				final int maxQueueSize = parseInt(args[2].toString());
				final int jobSplitThreshold = parseInt(args[3].toString());
				final int splittingFactor = parseInt(args[4].toString());

				this.configManagement = new SchedulerConfigurationManagement(this, deadlineWeight, powerWeight,
						maxQueueSize, jobSplitThreshold, splittingFactor);
				this.stateManagement = new SchedulerStateManagement(this);
				this.setUpPriorityQueue();
				register(this, SCHEDULER_SERVICE_TYPE, SCHEDULER_SERVICE_NAME);

			} catch (final NumberFormatException e) {
				logger.info("Weight arguments must be double values!");
				doDelete();
			}
		} else {
			logger.info("Incorrect arguments: some parameters for Scheduler Agent are missing");
			doDelete();
		}
	}

	/**
	 * Abstract method used to validate if arguments of the given agent are correct
	 */
	@Override
	protected void validateAgentArguments() {
		if (!isFibonacci((int) configManagement.getPowerWeightPriority()) ||
				!isFibonacci((int) configManagement.getDeadlineWeightPriority())) {
			logger.info("Incorrect arguments: Weights must be in a Fibonacci sequence");
			doDelete();
		}
		if (configManagement.getMaximumQueueSize() < 1) {
			logger.info("Incorrect arguments: Queue size must be a positive integer!");
			doDelete();
		}
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return List.of(
				SubscribeCloudNetworkService.create(this),
				new HandleJobAnnouncement(this),
				new ListenForClientJob(),
				new ListenForJobUpdate()
		);
	}
}

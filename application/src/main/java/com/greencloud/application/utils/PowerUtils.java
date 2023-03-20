package com.greencloud.application.utils;

import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static java.util.Objects.nonNull;

import java.util.Map;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.exception.IncorrectAgentTypeException;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;
import com.gui.agents.AbstractNetworkAgentNode;

/**
 * Class defines set of utilities used in state/power management
 */
public class PowerUtils {

	/**
	 * Method calculates the power in use at the given moment based on the given jobs
	 *
	 * @param jobMap map containing jobs and their statuses
	 * @return current power in use
	 */
	public static <T extends PowerJob> int getCurrentPowerInUse(final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.entrySet()
				.stream()
				.filter(job -> job.getValue().equals(IN_PROGRESS))
				.mapToInt(job -> job.getKey().getPower())
				.sum();
	}

	/**
	 * Method calculates the power in use at the given moment based on the given jobs
	 *
	 * @param jobMap map containing jobs and their statuses
	 * @return current power in use
	 */
	public static <T extends PowerJob> int getBackUpPowerInUse(final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.entrySet()
				.stream()
				.filter(job -> job.getValue().equals(IN_PROGRESS_BACKUP_ENERGY))
				.mapToInt(job -> job.getKey().getPower())
				.sum();
	}

	/**
	 * Method calculates the power percentage usage with respect to given maximum capacity
	 *
	 * @param traffic     used power
	 * @param maxCapacity maximal capacity
	 * @return power percentage
	 */
	public static double getPowerPercent(final int traffic, final int maxCapacity) {
		return maxCapacity == 0 ? 0 : (double) traffic / (double) maxCapacity;
	}

	/**
	 * Method calculates the power percentage usage with respect to given maximum capacity
	 *
	 * @param traffic     used power
	 * @param maxCapacity maximal capacity
	 * @return power percentage
	 */
	public static double getPowerPercent(final double traffic, final double maxCapacity) {
		return maxCapacity == 0 ? 0 : traffic / maxCapacity;
	}

	/**
	 * Method updates information regarding agent's maximum capacity
	 *
	 * @param newCapacity new maximum capacity
	 */
	public static void updateAgentMaximumCapacity(final double newCapacity, final AbstractAgent agent) {
		int powerInUse;

		if (agent instanceof CloudNetworkAgent cloudNetworkAgent) {
			cloudNetworkAgent.setMaximumCapacity(newCapacity);
			powerInUse = getCurrentPowerInUse(cloudNetworkAgent.getNetworkJobs());
		} else if (agent instanceof ServerAgent serverAgent) {
			serverAgent.setCurrentMaximumCapacity((int) newCapacity);
			powerInUse = getCurrentPowerInUse(serverAgent.getServerJobs());
		} else if (agent instanceof GreenEnergyAgent greenEnergyAgent) {
			greenEnergyAgent.setCurrentMaximumCapacity((int) newCapacity);
			powerInUse = getCurrentPowerInUse(greenEnergyAgent.getServerJobs());
		} else {
			throw new IncorrectAgentTypeException(agent.getClass().getTypeName());
		}

		if (nonNull(agent.getAgentNode())) {
			((AbstractNetworkAgentNode) agent.getAgentNode()).updateMaximumCapacity((int) newCapacity, powerInUse);
		}
	}
}

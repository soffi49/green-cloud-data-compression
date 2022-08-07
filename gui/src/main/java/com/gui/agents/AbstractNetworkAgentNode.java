package com.gui.agents;

import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL;
import static com.gui.gui.utils.GUILabelUtils.formatToHTML;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum;

/**
 * Class represents abstract generic agent node which is a part of cloud network
 */
public abstract class AbstractNetworkAgentNode extends AbstractAgentNode {

	protected final double initialMaximumCapacity;
	protected final AtomicReference<Double> currentMaximumCapacity;
	protected final AtomicBoolean isActive;
	protected final AtomicReference<Double> traffic;
	protected final AtomicInteger numberOfExecutedJobs;
	protected final AtomicInteger numberOfJobsOnHold;

	/**
	 * Network agent node constructor
	 *
	 * @param agentName       agent node name
	 * @param maximumCapacity maximum capacity of network agent
	 */
	protected AbstractNetworkAgentNode(final String agentName, final double maximumCapacity) {
		super(agentName);
		this.initialMaximumCapacity = maximumCapacity;
		this.currentMaximumCapacity = new AtomicReference<>(maximumCapacity);
		this.isActive = new AtomicBoolean(false);
		this.traffic = new AtomicReference<>(0D);
		this.numberOfExecutedJobs = new AtomicInteger(0);
		this.numberOfJobsOnHold = new AtomicInteger(0);
	}

	/**
	 * Function updates the current maximum capacity to given value
	 *
	 * @param maxCapacity new maximum capacity
	 */
	public void updateMaximumCapacity(final int maxCapacity) {
		this.currentMaximumCapacity.set((double) maxCapacity);
		this.traffic.set(currentMaximumCapacity.get() != 0 ? ((traffic.get() / maxCapacity) * 100) : 0);
		agentDetailLabels.get(CURRENT_MAXIMUM_CAPACITY_LABEL).setText(formatToHTML(String.valueOf(maxCapacity)));
		agentDetailLabels.get(AgentDetailsPanelListLabelEnum.TRAFFIC_LABEL)
				.setText(formatToHTML(String.format("%.2f%%", traffic.get())));
		updateGraphUI();
	}

	/**
	 * Function updates the current traffic for given value
	 *
	 * @param powerInUse current power in use
	 */
	public void updateTraffic(final double powerInUse) {
		this.traffic.set(currentMaximumCapacity.get() != 0 ? ((powerInUse / currentMaximumCapacity.get()) * 100) : 0);
		agentDetailLabels.get(AgentDetailsPanelListLabelEnum.TRAFFIC_LABEL)
				.setText(formatToHTML(String.format("%.2f%%", traffic.get())));
		updateGraphUI();
	}

	/**
	 * Function updates the information if the given network node is active
	 *
	 * @param isActive information if the network node is active
	 */
	public void updateIsActive(final boolean isActive) {
		this.isActive.set(isActive);
		agentDetailLabels.get(AgentDetailsPanelListLabelEnum.IS_ACTIVE_LABEL)
				.setText(formatToHTML(isActive ? "ACTIVE" : "INACTIVE"));
		updateGraphUI();
	}

	/**
	 * Function updates the number of currently executed jobs
	 *
	 * @param value new jobs count
	 */
	public void updateJobsCount(final int value) {
		this.numberOfExecutedJobs.set(value);
		agentDetailLabels.get(AgentDetailsPanelListLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL)
				.setText(formatToHTML(String.valueOf(numberOfExecutedJobs)));
	}

	/**
	 * Function updates the number of jobs being on hold to given value
	 *
	 * @param value number of jobs that are on hold
	 */
	public void updateJobsOnHoldCount(final int value) {
		numberOfJobsOnHold.set(value);
		agentDetailLabels.get(AgentDetailsPanelListLabelEnum.JOBS_ON_HOLD_LABEL)
				.setText(formatToHTML(String.valueOf(numberOfJobsOnHold)));
		updateGraphUI();
	}
}

package com.gui.domain.nodes;

import static com.gui.domain.types.AgentNodeLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL;
import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_ACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.SERVER_BACK_UP_POWER_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.SERVER_ON_HOLD_STYLE;
import static com.gui.utils.GUIUtils.createListLabel;
import static com.gui.utils.GUIUtils.formatToHTML;

import com.gui.domain.types.AgentNodeLabelEnum;
import com.gui.graph.domain.GraphStyleConstants;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent node class representing the server
 */
public class ServerAgentNode extends AgentNode {

	private final double initialMaximumCapacity;
	private final AtomicReference<Double> currentMaximumCapacity;
	private final String cloudNetworkAgent;
	private final List<String> greenEnergyAgents;
	private final AtomicBoolean isActive;
	private final AtomicBoolean isActiveBackUp;
	private final AtomicReference<Double> traffic;
	private final AtomicReference<Double> backUpTraffic;
	private final AtomicInteger totalNumberOfClients;
	private final AtomicInteger numberOfExecutedJobs;
	private final AtomicInteger numberOfJobsOnHold;

	/**
	 * Server node constructor
	 *
	 * @param name              node name
	 * @param maximumCapacity   maximum server capacity
	 * @param cloudNetworkAgent name of the owner cloud network
	 * @param greenEnergyAgents names of owned green sources
	 */
	public ServerAgentNode(
			String name,
			double maximumCapacity,
			String cloudNetworkAgent,
			List<String> greenEnergyAgents) {
		super(name);
		this.isActive = new AtomicBoolean(false);
		this.isActiveBackUp = new AtomicBoolean(false);
		this.initialMaximumCapacity = maximumCapacity;
		this.currentMaximumCapacity = new AtomicReference<>(maximumCapacity);
		this.cloudNetworkAgent = cloudNetworkAgent;
		this.totalNumberOfClients = new AtomicInteger(0);
		this.numberOfExecutedJobs = new AtomicInteger(0);
		this.numberOfJobsOnHold = new AtomicInteger(0);
		this.traffic = new AtomicReference<>(0D);
		this.backUpTraffic = new AtomicReference<>(0D);
		this.greenEnergyAgents = greenEnergyAgents;
		initializeLabelsMap();
		createInformationPanel();
	}

	/**
	 * Function updates the information if the given server is active and whether it has active backup
	 *
	 * @param isActive       information if the server is active
	 * @param isActiveBackUp information if the server has active backup power support
	 */
	public void updateIsActive(final boolean isActive, final boolean isActiveBackUp) {
		this.isActiveBackUp.set(isActiveBackUp);
		this.isActive.set(isActive);
		labelsMap.get(AgentNodeLabelEnum.IS_ACTIVE_LABEL).setText(formatToHTML(isActive ? "ACTIVE" : "INACTIVE"));
		updateGraphUI();
	}

	/**
	 * Function updates the number of clients
	 *
	 * @param value new clients count
	 */
	public void updateClientNumber(final int value) {
		this.totalNumberOfClients.set(value);
		labelsMap.get(AgentNodeLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL)
				.setText(formatToHTML(String.valueOf(totalNumberOfClients)));
	}

	/**
	 * Function updates the current traffic
	 *
	 * @param powerInUse current power in use
	 */
	public void updateTraffic(final double powerInUse) {
		this.traffic.set(currentMaximumCapacity.get() != 0 ? ((powerInUse / currentMaximumCapacity.get()) * 100) : 0);
		labelsMap.get(AgentNodeLabelEnum.TRAFFIC_LABEL).setText(formatToHTML(String.format("%.2f%%", traffic.get())));
		updateGraphUI();
	}

	/**
	 * Function updates the current back-up traffic
	 *
	 * @param backUpPowerInUse current power in use coming from back-up energy
	 */
	public void updateBackUpTraffic(final double backUpPowerInUse) {
		this.backUpTraffic.set(initialMaximumCapacity != 0 ? ((backUpPowerInUse / initialMaximumCapacity) * 100) : 0);
		labelsMap.get(AgentNodeLabelEnum.BACK_UP_TRAFFIC_LABEL)
				.setText(formatToHTML(String.format("%.2f%%", backUpTraffic.get())));
		updateGraphUI();
	}

	/**
	 * Function updates the number of currently executed jobs
	 *
	 * @param value new jobs count
	 */
	public void updateJobsCount(final int value) {
		this.numberOfExecutedJobs.set(value);
		labelsMap.get(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL)
				.setText(formatToHTML(String.valueOf(numberOfExecutedJobs)));
	}

	/**
	 * Function updates the number of jobs being on hold
	 *
	 * @param value new on hold jobs count
	 */
	public void updateOnHoldJobsCount(final int value) {
		this.numberOfJobsOnHold.set(value);
		labelsMap.get(AgentNodeLabelEnum.JOBS_ON_HOLD_LABEL).setText(formatToHTML(String.valueOf(numberOfJobsOnHold)));
		updateGraphUI();
	}

	/**
	 * Function updates the current maximum capacity
	 *
	 * @param maxCapacity new maximum capacity
	 */
	public void updateMaximumCapacity(final int maxCapacity) {
		this.currentMaximumCapacity.set((double) maxCapacity);
		this.traffic.set(currentMaximumCapacity.get() != 0 ? ((traffic.get() / maxCapacity) * 100) : 0);
		labelsMap.get(CURRENT_MAXIMUM_CAPACITY_LABEL).setText(formatToHTML(String.valueOf(maxCapacity)));
		labelsMap.get(AgentNodeLabelEnum.TRAFFIC_LABEL).setText(formatToHTML(String.format("%.2f%%", traffic.get())));
		updateGraphUI();
	}

	@Override
	public synchronized void updateGraphUI() {
		final String style = numberOfJobsOnHold.get() > 0 ? SERVER_ON_HOLD_STYLE
				: (isActiveBackUp.get() ?
				SERVER_BACK_UP_POWER_STYLE
				:
				(isActive.get() ? GraphStyleConstants.SERVER_ACTIVE_STYLE : GraphStyleConstants.SERVER_INACTIVE_STYLE));
		final String edgeStyle = isActive.get() ? CONNECTOR_EDGE_ACTIVE_STYLE : CONNECTOR_EDGE_STYLE;
		graphService.updateNodeStyle(name, style);
		graphService.updateEdgeStyle(name, cloudNetworkAgent, false, edgeStyle);
	}

	@Override
	public void createEdges() {
		graphService.createAndAddEdgeToGraph(name, cloudNetworkAgent, false);
		greenEnergyAgents.forEach(greenEnergyName -> graphService.createAndAddEdgeToGraph(name, greenEnergyName, true));
		graphService.createAndAddEdgeToGraph(name, cloudNetworkAgent, true);
	}

	@Override
	protected void initializeLabelsMap() {
		super.initializeLabelsMap();
		labelsMap.put(AgentNodeLabelEnum.IS_ACTIVE_LABEL, createListLabel(isActive.get() ? "ACTIVE" : "INACTIVE"));
		labelsMap.put(AgentNodeLabelEnum.INITIAL_MAXIMUM_CAPACITY_LABEL,
				createListLabel(String.valueOf(initialMaximumCapacity)));
		labelsMap.put(AgentNodeLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL,
				createListLabel(String.valueOf(currentMaximumCapacity)));
		labelsMap.put(AgentNodeLabelEnum.TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
		labelsMap.put(AgentNodeLabelEnum.BACK_UP_TRAFFIC_LABEL,
				createListLabel(String.format("%.2f%%", backUpTraffic.get())));
		labelsMap.put(AgentNodeLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL,
				createListLabel(String.valueOf(totalNumberOfClients)));
		labelsMap.put(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL,
				createListLabel(String.valueOf(numberOfExecutedJobs)));
		labelsMap.put(AgentNodeLabelEnum.JOBS_ON_HOLD_LABEL, createListLabel(String.valueOf(numberOfJobsOnHold)));
	}
}

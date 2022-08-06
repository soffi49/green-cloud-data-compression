package com.gui.domain.nodes;

import static com.gui.domain.types.AgentNodeLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL;
import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_ACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.GREEN_SOURCE_ACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.GREEN_SOURCE_INACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.GREEN_SOURCE_ON_HOLD_STYLE;
import static com.gui.utils.GUIUtils.createListLabel;
import static com.gui.utils.GUIUtils.formatToHTML;

import com.gui.domain.Location;
import com.gui.domain.types.AgentNodeLabelEnum;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent node class representing the green energy source
 */
public class GreenEnergyAgentNode extends AgentNode {

	private final Location location;
	private final double initialMaximumCapacity;
	private final AtomicReference<Double> currentMaximumCapacity;
	private final String monitoringAgent;
	private final String serverAgent;
	private final AtomicBoolean isActive;
	private final AtomicBoolean hasJobsOnHold;
	private final AtomicReference<Double> traffic;
	private final AtomicInteger jobsOnHold;
	private final AtomicInteger numberOfExecutedJobs;

	/**
	 * Green energy source node constructor
	 *
	 * @param name            node name
	 * @param maximumCapacity maximum capacity of green source
	 * @param serverAgent     owner server name
	 * @param monitoringAgent connected monitoring agent
	 */
	public GreenEnergyAgentNode(
			String name,
			double maximumCapacity,
			String monitoringAgent,
			String serverAgent,
			Location location) {
		super(name);
		this.initialMaximumCapacity = maximumCapacity;
		this.currentMaximumCapacity = new AtomicReference<>(maximumCapacity);
		this.location = location;
		this.serverAgent = serverAgent;
		this.monitoringAgent = monitoringAgent;
		this.isActive = new AtomicBoolean(false);
		this.hasJobsOnHold = new AtomicBoolean(false);
		this.traffic = new AtomicReference<>(0D);
		this.jobsOnHold = new AtomicInteger(0);
		this.numberOfExecutedJobs = new AtomicInteger(0);
		initializeLabelsMap();
		createInformationPanel();
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
	 * Function updates the current traffic on hold
	 *
	 * @param value number of jobs that are on hold
	 */
	public void updateJobsOnHold(final int value) {
		jobsOnHold.set(value);
		labelsMap.get(AgentNodeLabelEnum.JOBS_ON_HOLD_LABEL).setText(formatToHTML(String.valueOf(jobsOnHold)));
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
	 * Function updates the information if the given green source is active and whether there are any
	 * jobs on hold
	 *
	 * @param isActive information if the green source is active
	 */
	public void updateIsActive(final boolean isActive, final boolean hasJobsOnHold) {
		this.hasJobsOnHold.set(hasJobsOnHold);
		this.isActive.set(isActive);
		labelsMap.get(AgentNodeLabelEnum.IS_ACTIVE_LABEL).setText(formatToHTML(isActive ? "ACTIVE" : "INACTIVE"));
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
	public void updateGraphUI() {
		final String style = hasJobsOnHold.get() ?
				GREEN_SOURCE_ON_HOLD_STYLE :
				(isActive.get() ? GREEN_SOURCE_ACTIVE_STYLE : GREEN_SOURCE_INACTIVE_STYLE);
		final String edgeStyle = isActive.get() ? CONNECTOR_EDGE_ACTIVE_STYLE : CONNECTOR_EDGE_STYLE;
		graphService.updateNodeStyle(name, style);
		graphService.updateEdgeStyle(name, serverAgent, false, edgeStyle);
	}

	@Override
	public void createEdges() {
		graphService.createAndAddEdgeToGraph(name, serverAgent, false);
		graphService.createAndAddEdgeToGraph(name, monitoringAgent, false);
		graphService.createAndAddEdgeToGraph(name, monitoringAgent, true);
		graphService.createAndAddEdgeToGraph(name, serverAgent, true);
	}

	@Override
	protected void initializeLabelsMap() {
		super.initializeLabelsMap();
		labelsMap.put(AgentNodeLabelEnum.IS_ACTIVE_LABEL, createListLabel(isActive.get() ? "ACTIVE" : "INACTIVE"));
		labelsMap.put(AgentNodeLabelEnum.LOCATION_LATITUDE_LABEL, createListLabel(location.getLatitude()));
		labelsMap.put(AgentNodeLabelEnum.LOCATION_LONGITUDE_LABEL, createListLabel(location.getLongitude()));
		labelsMap.put(AgentNodeLabelEnum.INITIAL_MAXIMUM_CAPACITY_LABEL,
				createListLabel(String.valueOf(initialMaximumCapacity)));
		labelsMap.put(CURRENT_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(currentMaximumCapacity)));
		labelsMap.put(AgentNodeLabelEnum.TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
		labelsMap.put(AgentNodeLabelEnum.JOBS_ON_HOLD_LABEL, createListLabel(String.valueOf(jobsOnHold.get())));
		labelsMap.put(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL,
				createListLabel(String.valueOf(numberOfExecutedJobs)));
	}
}

package com.gui.agents;

import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_ACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.SERVER_BACK_UP_POWER_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.SERVER_ON_HOLD_STYLE;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.BACK_UP_TRAFFIC_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.INITIAL_MAXIMUM_CAPACITY_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.IS_ACTIVE_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.JOBS_ON_HOLD_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.TRAFFIC_LABEL;
import static com.gui.gui.utils.GUILabelUtils.createListLabel;
import static com.gui.gui.utils.GUILabelUtils.formatToHTML;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.gui.event.domain.EventTypeEnum;
import com.gui.graph.domain.GraphStyleConstants;

/**
 * Agent node class representing the server
 */
public class ServerAgentNode extends AbstractNetworkAgentNode {

	private final String cloudNetworkAgent;
	private final List<String> greenEnergyAgents;
	private final AtomicReference<Double> backUpTraffic;
	private final AtomicInteger totalNumberOfClients;

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
		super(name, maximumCapacity);
		this.cloudNetworkAgent = cloudNetworkAgent;
		this.greenEnergyAgents = greenEnergyAgents;
		this.totalNumberOfClients = new AtomicInteger(0);
		this.backUpTraffic = new AtomicReference<>(0D);
		assignAgentEvents();
		initializeLabelsMap();
		createInformationPanel();
	}

	/**
	 * Function updates the current back-up traffic to given value
	 *
	 * @param backUpPowerInUse current power in use coming from back-up energy
	 */
	public void updateBackUpTraffic(final double backUpPowerInUse) {
		this.backUpTraffic.set(initialMaximumCapacity != 0 ? ((backUpPowerInUse / initialMaximumCapacity) * 100) : 0);
		agentDetailLabels.get(BACK_UP_TRAFFIC_LABEL)
				.setText(formatToHTML(String.format("%.2f%%", backUpTraffic.get())));
		updateGraphUI();
	}

	/**
	 * Function updates the number of clients
	 *
	 * @param value new clients count
	 */
	public void updateClientNumber(final int value) {
		this.totalNumberOfClients.set(value);
		agentDetailLabels.get(TOTAL_NUMBER_OF_CLIENTS_LABEL)
				.setText(formatToHTML(String.valueOf(totalNumberOfClients)));
	}

	@Override
	public synchronized void updateGraphUI() {
		final String style = numberOfJobsOnHold.get() > 0 ? SERVER_ON_HOLD_STYLE :
				(backUpTraffic.get() > 0 ? SERVER_BACK_UP_POWER_STYLE : (isActive.get() ?
						GraphStyleConstants.SERVER_ACTIVE_STYLE :
						GraphStyleConstants.SERVER_INACTIVE_STYLE));
		final String edgeStyle = isActive.get() ? CONNECTOR_EDGE_ACTIVE_STYLE : CONNECTOR_EDGE_STYLE;
		graphService.updateNodeStyle(agentName, style);
		graphService.updateEdgeStyle(agentName, cloudNetworkAgent, false, edgeStyle);
	}

	@Override
	public void createEdges() {
		graphService.createAndAddEdgeToGraph(agentName, cloudNetworkAgent, false);
		greenEnergyAgents.forEach(
				greenEnergyName -> graphService.createAndAddEdgeToGraph(agentName, greenEnergyName, true));
		graphService.createAndAddEdgeToGraph(agentName, cloudNetworkAgent, true);
	}

	@Override
	public void initializeLabelsMap() {
		agentDetailLabels.put(IS_ACTIVE_LABEL, createListLabel(isActive.get() ? "ACTIVE" : "INACTIVE"));
		agentDetailLabels.put(INITIAL_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(initialMaximumCapacity)));
		agentDetailLabels.put(CURRENT_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(currentMaximumCapacity)));
		agentDetailLabels.put(TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
		agentDetailLabels.put(BACK_UP_TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", backUpTraffic.get())));
		agentDetailLabels.put(TOTAL_NUMBER_OF_CLIENTS_LABEL, createListLabel(String.valueOf(totalNumberOfClients)));
		agentDetailLabels.put(NUMBER_OF_EXECUTED_JOBS_LABEL, createListLabel(String.valueOf(numberOfExecutedJobs)));
		agentDetailLabels.put(JOBS_ON_HOLD_LABEL, createListLabel(String.valueOf(numberOfJobsOnHold)));
	}

	private void assignAgentEvents() {
		agentEvents.put(EventTypeEnum.POWER_SHORTAGE, false);
	}
}

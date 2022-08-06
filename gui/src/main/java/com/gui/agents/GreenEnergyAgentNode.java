package com.gui.agents;

import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_ACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.CONNECTOR_EDGE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.GREEN_SOURCE_ACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.GREEN_SOURCE_INACTIVE_STYLE;
import static com.gui.graph.domain.GraphStyleConstants.GREEN_SOURCE_ON_HOLD_STYLE;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.INITIAL_MAXIMUM_CAPACITY_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.IS_ACTIVE_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.JOBS_ON_HOLD_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.LOCATION_LATITUDE_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.LOCATION_LONGITUDE_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL;
import static com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum.TRAFFIC_LABEL;
import static com.gui.gui.utils.GUILabelUtils.createListLabel;

import com.gui.agents.domain.AgentLocation;
import com.gui.event.domain.EventTypeEnum;

/**
 * Agent node class representing the green energy source
 */
public class GreenEnergyAgentNode extends AbstractNetworkAgentNode {

	private final AgentLocation agentLocation;
	private final String monitoringAgent;
	private final String serverAgent;

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
			AgentLocation agentLocation) {
		super(name, maximumCapacity);
		this.agentLocation = agentLocation;
		this.serverAgent = serverAgent;
		this.monitoringAgent = monitoringAgent;
		assignAgentEvents();
		initializeLabelsMap();
		createInformationPanel();
	}

	@Override
	public void updateGraphUI() {
		final String style = numberOfJobsOnHold.get() > 0 ?
				GREEN_SOURCE_ON_HOLD_STYLE :
				(isActive.get() ? GREEN_SOURCE_ACTIVE_STYLE : GREEN_SOURCE_INACTIVE_STYLE);
		final String edgeStyle = isActive.get() ? CONNECTOR_EDGE_ACTIVE_STYLE : CONNECTOR_EDGE_STYLE;
		graphService.updateNodeStyle(agentName, style);
		graphService.updateEdgeStyle(agentName, serverAgent, false, edgeStyle);
	}

	@Override
	public void createEdges() {
		graphService.createAndAddEdgeToGraph(agentName, serverAgent, false);
		graphService.createAndAddEdgeToGraph(agentName, monitoringAgent, false);
		graphService.createAndAddEdgeToGraph(agentName, monitoringAgent, true);
		graphService.createAndAddEdgeToGraph(agentName, serverAgent, true);
	}

	@Override
	public void initializeLabelsMap() {
		agentDetailLabels.put(IS_ACTIVE_LABEL, createListLabel(isActive.get() ? "ACTIVE" : "INACTIVE"));
		agentDetailLabels.put(LOCATION_LATITUDE_LABEL, createListLabel(agentLocation.getLatitude()));
		agentDetailLabels.put(LOCATION_LONGITUDE_LABEL, createListLabel(agentLocation.getLongitude()));
		agentDetailLabels.put(INITIAL_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(initialMaximumCapacity)));
		agentDetailLabels.put(CURRENT_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(currentMaximumCapacity)));
		agentDetailLabels.put(TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
		agentDetailLabels.put(JOBS_ON_HOLD_LABEL, createListLabel(String.valueOf(numberOfJobsOnHold.get())));
		agentDetailLabels.put(NUMBER_OF_EXECUTED_JOBS_LABEL, createListLabel(String.valueOf(numberOfExecutedJobs)));
	}

	private void assignAgentEvents() {
		agentEvents.put(EventTypeEnum.POWER_SHORTAGE, false);
	}
}

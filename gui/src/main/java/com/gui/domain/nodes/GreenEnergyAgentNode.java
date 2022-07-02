package com.gui.domain.nodes;

import static com.gui.utils.GUIUtils.*;
import static com.gui.utils.GraphUtils.*;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.Location;
import com.gui.domain.types.AgentNodeLabelEnum;
import org.graphstream.graph.Graph;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent node class representing the green energy source
 */
public class GreenEnergyAgentNode extends AgentNode {

    private final Location location;
    private final AtomicReference<Double> maximumCapacity;
    private final String monitoringAgent;
    private final String serverAgent;
    private final AtomicBoolean isActive;
    private final AtomicReference<Double> traffic;
    private final AtomicInteger numberOfExecutedJobs;

    /**
     * Green energy source node constructor
     *
     * @param name            node name
     * @param maximumCapacity maximum capacity of green source
     * @param serverAgent     owner server name
     * @param monitoringAgent connected monitoring agent
     */
    public GreenEnergyAgentNode(String name, double maximumCapacity, String monitoringAgent, String serverAgent, Location location) {
        super(name);
        this.maximumCapacity = new AtomicReference<>(maximumCapacity);
        this.location = location;
        this.serverAgent = serverAgent;
        this.monitoringAgent = monitoringAgent;
        this.style = GREEN_ENERGY_STYLE;
        this.isActive = new AtomicBoolean(false);
        this.traffic = new AtomicReference<>(0D);
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
        this.traffic.set((powerInUse / maximumCapacity.get()) * 100);
        labelsMap.get(AgentNodeLabelEnum.TRAFFIC_LABEL).setText(formatToHTML(String.format("%.2f%%", traffic.get())));
        updateGraphUI();
    }

    /**
     * Function updates the number of currently executed jobs by given value
     *
     * @param value value to be added to the number of jobs being executed
     */
    public void updateJobsCount(final int value) {
        this.numberOfExecutedJobs.getAndAdd(value);
        labelsMap.get(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL).setText(formatToHTML(String.valueOf(numberOfExecutedJobs)));
    }

    /**
     * Function updates the information if the given green source is active
     *
     * @param isActive information if the green source is active
     */
    public void updateIsActive(final boolean isActive) {
        this.isActive.set(isActive);
        labelsMap.get(AgentNodeLabelEnum.IS_ACTIVE_LABEL).setText(formatToHTML(isActive ? "ACTIVE" : "INACTIVE"));
        updateGraphUI();
    }

    @Override
    public void updateGraphUI() {
        final String dynamicStyle = isActive.get() ? GREEN_ENERGY_ACTIVE_STYLE : GREEN_ENERGY_INACTIVE_STYLE;
        node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style, dynamicStyle)));
        updateActiveEdgeStyle(edges, isActive.get(), name, serverAgent);
    }

    @Override
    public void createEdges(final Graph graph) {
        addAgentBidirectionalEdgeToGraph(graph, edges, name, serverAgent);
        addAgentBidirectionalEdgeToGraph(graph, edges, name, monitoringAgent);
        addAgentEdgeToGraph(graph, edges, name, monitoringAgent);
        addAgentEdgeToGraph(graph, edges, name, serverAgent);
    }

    @Override
    protected void initializeLabelsMap() {
        super.initializeLabelsMap();
        labelsMap.put(AgentNodeLabelEnum.IS_ACTIVE_LABEL, createListLabel(isActive.get() ? "ACTIVE" : "INACTIVE"));
        labelsMap.put(AgentNodeLabelEnum.LOCATION_LATITUDE_LABEL, createListLabel(location.getLatitude()));
        labelsMap.put(AgentNodeLabelEnum.LOCATION_LONGITUDE_LABEL, createListLabel(location.getLongitude()));
        labelsMap.put(AgentNodeLabelEnum.MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(maximumCapacity)));
        labelsMap.put(AgentNodeLabelEnum.TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
        labelsMap.put(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL, createListLabel(String.valueOf(numberOfExecutedJobs)));
    }
}

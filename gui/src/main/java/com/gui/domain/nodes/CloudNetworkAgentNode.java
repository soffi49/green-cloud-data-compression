package com.gui.domain.nodes;

import static com.gui.utils.GUIUtils.*;
import static com.gui.utils.GraphUtils.addAgentEdgeToGraph;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.types.AgentNodeLabelEnum;
import org.graphstream.graph.Graph;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent node class representing the cloud network
 */
public class CloudNetworkAgentNode extends AgentNode {

    private final AtomicReference<Double> maximumCapacity;
    private final List<String> serverAgents;
    private final AtomicReference<Double> traffic;
    private final AtomicInteger totalNumberOfClients;
    private final AtomicInteger numberOfExecutedJobs;

    /**
     * Cloud network node constructor
     *
     * @param name            name of the node
     * @param maximumCapacity maximum capacity of cloud network
     * @param serverAgents    list of server agents names
     */
    public CloudNetworkAgentNode(String name, double maximumCapacity, List<String> serverAgents) {
        super(name);
        this.maximumCapacity = new AtomicReference<>(maximumCapacity);
        this.serverAgents = serverAgents;
        this.style = CLOUD_NETWORK_STYLE;
        this.traffic = new AtomicReference<>(0D);
        this.totalNumberOfClients = new AtomicInteger(0);
        this.numberOfExecutedJobs = new AtomicInteger(0);
        initializeLabelsMap();
        createInformationPanel();
    }

    /**
     * Function updates the number of clients by given value
     *
     * @param value value to be added to client number
     */
    public void updateClientNumber(final int value) {
        this.totalNumberOfClients.getAndAdd(value);
        labelsMap.get(AgentNodeLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL).setText(formatToHTML(String.valueOf(totalNumberOfClients)));
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

    @Override
    public void updateGraphUI() {
        if (traffic.get() > 85) {
            node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style, CLOUD_NETWORK_HIGH_STYLE)));
        } else if (traffic.get() > 50) {
            node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style, CLOUD_NETWORK_MEDIUM_STYLE)));
        } else if (traffic.get() > 0) {
            node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style, CLOUD_NETWORK_LOW_STYLE)));
        } else {
            node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style, CLOUD_NETWORK_INACTIVE_STYLE)));
        }
    }

    @Override
    public void createEdges(Graph graph) {
        serverAgents.forEach(serverName -> addAgentEdgeToGraph(graph, edges, name, serverName));
    }

    @Override
    protected void initializeLabelsMap() {
        super.initializeLabelsMap();
        labelsMap.put(AgentNodeLabelEnum.SERVERS_NUMBER_LABEL, createListLabel(String.valueOf(serverAgents.size())));
        labelsMap.put(AgentNodeLabelEnum.MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(maximumCapacity)));
        labelsMap.put(AgentNodeLabelEnum.TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
        labelsMap.put(AgentNodeLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL, createListLabel(String.valueOf(totalNumberOfClients)));
        labelsMap.put(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL, createListLabel(String.valueOf(numberOfExecutedJobs)));
    }

}

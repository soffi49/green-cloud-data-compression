package com.gui.domain.nodes;

import static com.gui.domain.types.AgentNodeLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL;
import static com.gui.utils.GUIUtils.concatenateStyles;
import static com.gui.utils.GUIUtils.createListLabel;
import static com.gui.utils.GUIUtils.formatToHTML;
import static com.gui.utils.GraphUtils.addAgentBidirectionalEdgeToGraph;
import static com.gui.utils.GraphUtils.addAgentEdgeToGraph;
import static com.gui.utils.GraphUtils.createSpriteForNode;
import static com.gui.utils.GraphUtils.updateActiveEdgeStyle;
import static com.gui.utils.domain.StyleConstants.LABEL_STYLE;
import static com.gui.utils.domain.StyleConstants.SERVER_ACTIVE_BACK_UP_STYLE;
import static com.gui.utils.domain.StyleConstants.SERVER_ACTIVE_STYLE;
import static com.gui.utils.domain.StyleConstants.SERVER_INACTIVE_STYLE;
import static com.gui.utils.domain.StyleConstants.SERVER_STYLE;

import com.gui.domain.types.AgentNodeLabelEnum;
import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.Sprite;

import java.util.List;
import java.util.Objects;
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
    private Sprite warningSprite;

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
        this.style = SERVER_STYLE;
        this.isActive = new AtomicBoolean(false);
        this.isActiveBackUp = new AtomicBoolean(false);
        this.initialMaximumCapacity = maximumCapacity;
        this.currentMaximumCapacity = new AtomicReference<>(maximumCapacity);
        this.cloudNetworkAgent = cloudNetworkAgent;
        this.totalNumberOfClients = new AtomicInteger(0);
        this.numberOfExecutedJobs = new AtomicInteger(0);
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
        labelsMap.get(AgentNodeLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL).setText(formatToHTML(String.valueOf(totalNumberOfClients)));
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
        labelsMap.get(AgentNodeLabelEnum.BACK_UP_TRAFFIC_LABEL).setText(formatToHTML(String.format("%.2f%%", backUpTraffic.get())));
        updateGraphUI();
    }

    /**
     * Function updates the number of currently executed jobs
     *
     * @param value new jobs count
     */
    public void updateJobsCount(final int value) {
        this.numberOfExecutedJobs.set(value);
        labelsMap.get(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL).setText(formatToHTML(String.valueOf(numberOfExecutedJobs)));
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
        final String dynamicNodeStyle = isActiveBackUp.get() ? SERVER_ACTIVE_BACK_UP_STYLE : (isActive.get() ? SERVER_ACTIVE_STYLE : SERVER_INACTIVE_STYLE);
        if (Objects.nonNull(warningSprite)) {
            //final String dynamicSpriteStyle = isActiveBackUp.get() ? SERVER_ACTIVE_BACK_UP_STYLE : SPRITE_DISABLED;
            //warningSprite.setAttribute("ui.class", dynamicSpriteStyle);
        }
        synchronized (graph) {
            node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style, dynamicNodeStyle)));
        }
        updateActiveEdgeStyle(edges, graph, isActive.get(), name, cloudNetworkAgent);
    }

    @Override
    public void createEdges(final Graph graph) {
        addAgentBidirectionalEdgeToGraph(graph, edges, name, cloudNetworkAgent);
        greenEnergyAgents.forEach(
                greenEnergyName -> addAgentEdgeToGraph(graph, edges, name, greenEnergyName));
        addAgentEdgeToGraph(graph, edges, name, cloudNetworkAgent);
        warningSprite = createSpriteForNode(graph, node);
    }

    @Override
    protected void initializeLabelsMap() {
        super.initializeLabelsMap();
        labelsMap.put(AgentNodeLabelEnum.IS_ACTIVE_LABEL, createListLabel(isActive.get() ? "ACTIVE" : "INACTIVE"));
        labelsMap.put(AgentNodeLabelEnum.INITIAL_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(initialMaximumCapacity)));
        labelsMap.put(AgentNodeLabelEnum.CURRENT_MAXIMUM_CAPACITY_LABEL, createListLabel(String.valueOf(currentMaximumCapacity)));
        labelsMap.put(AgentNodeLabelEnum.TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", traffic.get())));
        labelsMap.put(AgentNodeLabelEnum.BACK_UP_TRAFFIC_LABEL, createListLabel(String.format("%.2f%%", backUpTraffic.get())));
        labelsMap.put(AgentNodeLabelEnum.TOTAL_NUMBER_OF_CLIENTS_LABEL, createListLabel(String.valueOf(totalNumberOfClients)));
        labelsMap.put(AgentNodeLabelEnum.NUMBER_OF_EXECUTED_JOBS_LABEL, createListLabel(String.valueOf(numberOfExecutedJobs)));
    }
}

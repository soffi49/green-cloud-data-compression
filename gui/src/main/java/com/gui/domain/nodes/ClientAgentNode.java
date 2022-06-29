package com.gui.domain.nodes;

import static com.gui.domain.types.JobStatusEnum.CREATED;
import static com.gui.utils.GUIUtils.createJLabel;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.types.AgentNodeLabelEnum;
import com.gui.domain.types.JobStatusEnum;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Agent node class representing the client
 */
public class ClientAgentNode extends AgentNode {

    private final String jobId;
    private final String startDate;
    private final String endDate;
    private JobStatusEnum jobStatusEnum;

    /**
     * Client node constructor
     *
     * @param name      name of the node
     * @param jobId     job identifier
     * @param startDate start date of job execution
     * @param endDate   end date of job execution
     */
    public ClientAgentNode(String name, String jobId, String startDate, String endDate) {
        super(name);
        this.jobId = jobId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.jobStatusEnum = CREATED;
        this.style = CLIENT_STYLE;
        initializeLabelsMap();
        createInformationPanel();
    }

    /**
     * Function overrides the job status
     *
     * @param jobStatusEnum new job status
     */
    public synchronized void updateJobStatus(final JobStatusEnum jobStatusEnum) {
        this.jobStatusEnum = jobStatusEnum;
        labelsMap.get(AgentNodeLabelEnum.JOB_STATUS).setText(jobStatusEnum.getStatus());
    }

    @Override
    public Node addToGraph(Graph graph) {
        final Node node = super.addToGraph(graph);
        node.setAttribute("ui.hide", "true");
        return node;
    }

    @Override
    protected void initializeLabelsMap() {
        super.initializeLabelsMap();
        labelsMap.put(AgentNodeLabelEnum.JOB_ID_LABEL, createJLabel(SECOND_HEADER_FONT, BLUE_COLOR, jobId));
        labelsMap.put(AgentNodeLabelEnum.JOB_START_LABEL, createJLabel(SECOND_HEADER_FONT, BLUE_COLOR, startDate));
        labelsMap.put(AgentNodeLabelEnum.JOB_END_LABEL, createJLabel(SECOND_HEADER_FONT, BLUE_COLOR, endDate));
        labelsMap.put(AgentNodeLabelEnum.JOB_STATUS, createJLabel(SECOND_HEADER_FONT, BLUE_COLOR, jobStatusEnum.getStatus()));
    }
}

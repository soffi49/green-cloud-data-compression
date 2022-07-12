package com.gui.domain.nodes;

import static com.gui.domain.types.JobStatusEnum.CREATED;
import static com.gui.utils.GUIUtils.*;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.types.AgentNodeLabelEnum;
import com.gui.domain.types.JobStatusEnum;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent node class representing the client
 */
public class ClientAgentNode extends AgentNode {

    private final String jobId;
    private final String  power;
    private final String startDate;
    private final String endDate;
    private AtomicReference<JobStatusEnum> jobStatusEnum;

    /**
     * Client node constructor
     *
     * @param name      name of the node
     * @param jobId     job identifier
     * @param startDate start date of job execution
     * @param endDate   end date of job execution
     */
    public ClientAgentNode(String name, String jobId, String startDate, String endDate, String power) {
        super(name);
        this.jobId = jobId;
        this.power = power;
        this.startDate = startDate;
        this.endDate = endDate;
        this.jobStatusEnum = new AtomicReference<>(CREATED);
        this.style = CLIENT_STYLE;
        initializeLabelsMap();
        createInformationPanel();
    }

    /**
     * Function overrides the job status
     *
     * @param jobStatusEnum new job status
     */
    public void updateJobStatus(final JobStatusEnum jobStatusEnum) {
        this.jobStatusEnum.set(jobStatusEnum);
        labelsMap.get(AgentNodeLabelEnum.JOB_STATUS).setText(formatToHTML(jobStatusEnum.getStatus()));
    }

    @Override
    public Node addToGraph(Graph graph) {
        return null;
    }

    @Override
    protected void initializeLabelsMap() {
        super.initializeLabelsMap();
        labelsMap.put(AgentNodeLabelEnum.JOB_ID_LABEL, createListLabel(jobId));
        labelsMap.put(AgentNodeLabelEnum.JOB_POWER, createListLabel(power));
        labelsMap.put(AgentNodeLabelEnum.JOB_START_LABEL, createListLabel(startDate));
        labelsMap.put(AgentNodeLabelEnum.JOB_END_LABEL, createListLabel(endDate));
        labelsMap.put(AgentNodeLabelEnum.JOB_STATUS, createListLabel(jobStatusEnum.get().getStatus()));
    }
}

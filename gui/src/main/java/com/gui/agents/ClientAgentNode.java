package com.gui.agents;

import static com.gui.agents.domain.JobStatusEnum.CREATED;
import static com.gui.gui.utils.GUILabelUtils.createListLabel;
import static com.gui.gui.utils.GUILabelUtils.formatToHTML;

import java.util.concurrent.atomic.AtomicReference;

import com.gui.agents.domain.JobStatusEnum;
import com.gui.graph.GraphService;
import com.gui.gui.panels.domain.listlabels.AgentDetailsPanelListLabelEnum;

/**
 * Agent node class representing the client
 */
public class ClientAgentNode extends AbstractAgentNode {

	private final String jobId;
	private final String power;
	private final String startDate;
	private final String endDate;
	private final AtomicReference<JobStatusEnum> jobStatusEnum;

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
		agentDetailLabels.get(AgentDetailsPanelListLabelEnum.JOB_STATUS)
				.setText(formatToHTML(jobStatusEnum.getStatus()));
	}

	@Override
	public void addToGraph(GraphService testGraph) {
	}

	@Override
	public void createEdges() {
	}

	@Override
	public void updateGraphUI() {
	}

	@Override
	public void initializeLabelsMap() {
		agentDetailLabels.put(AgentDetailsPanelListLabelEnum.JOB_ID_LABEL, createListLabel(jobId));
		agentDetailLabels.put(AgentDetailsPanelListLabelEnum.JOB_POWER, createListLabel(power));
		agentDetailLabels.put(AgentDetailsPanelListLabelEnum.JOB_START_LABEL, createListLabel(startDate));
		agentDetailLabels.put(AgentDetailsPanelListLabelEnum.JOB_END_LABEL, createListLabel(endDate));
		agentDetailLabels.put(AgentDetailsPanelListLabelEnum.JOB_STATUS,
				createListLabel(jobStatusEnum.get().getStatus()));
	}
}

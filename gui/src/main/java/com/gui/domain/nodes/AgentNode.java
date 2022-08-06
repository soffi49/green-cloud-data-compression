package com.gui.domain.nodes;

import static com.gui.utils.GUIUtils.createLabelListPanel;

import com.gui.domain.event.AbstractEvent;
import com.gui.domain.types.LabelEnum;
import com.gui.graph.GraphService;
import com.mxgraph.model.mxCell;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class represents abstract agent node
 */
public abstract class AgentNode {

	protected String name;
	protected JPanel informationPanel;
	protected Map<LabelEnum, JLabel> labelsMap;
	protected AtomicReference<AbstractEvent> event;
	protected boolean isDuringEvent;
	protected boolean isManualEventEnabled;
	protected GraphService graphService;

	/**
	 * Class constructor
	 *
	 * @param name name of the agent
	 */
	public AgentNode(String name) {
		this.name = name;
		this.event = new AtomicReference<>(null);
		this.isDuringEvent = false;
		this.isManualEventEnabled = true;
	}

	/**
	 * Abstract method responsible for adding the node to the graph
	 */
	public void addToGraph(final GraphService graphService) {
		this.graphService = graphService;
		graphService.createAndAddNodeToGraph(this);
		this.updateGraphUI();
	}

	/**
	 * Abstract method responsible for creating edges for given node
	 */
	public void createEdges() {
	}

	/**
	 * Abstract method which based on the agent status creates the JPanel displaying all data
	 */
	public void createInformationPanel() {
		this.informationPanel = createLabelListPanel(labelsMap);
	}

	/**
	 * Abstract method responsible for updating graph style based on the internal state of agent node
	 */
	public void updateGraphUI() {
	}

	/**
	 * Abstract method used to initialize labels map for given agent node
	 */
	protected void initializeLabelsMap() {
		this.labelsMap = new LinkedHashMap<>();
	}

	/**
	 * @return agent node name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return information panel of agent node
	 */
	public JPanel getInformationPanel() {
		return informationPanel;
	}

	/**
	 * @return gets the current event
	 */
	public AbstractEvent getEvent() {
		return event.get();
	}

	/**
	 * Set the event
	 *
	 * @param event new event
	 */
	public void setEvent(AbstractEvent event) {
		this.event.set(event);
	}

	/**
	 * @return boolean flag of having an event
	 */
	public boolean isDuringEvent() {
		return isDuringEvent;
	}

	/**
	 * Setting flag indicating if node has some event going on
	 *
	 * @param duringEvent event flag
	 */
	public void setDuringEvent(boolean duringEvent) {
		isDuringEvent = duringEvent;
	}

	/**
	 * Flag indicating if the user can invoke manually some event
	 *
	 * @return boolean flag
	 */
	public boolean isManualEventEnabled() {
		return isManualEventEnabled;
	}

	/**
	 * Setting flag indicating if the user can invoke manually some event
	 *
	 * @param manualEventEnabled flag
	 */
	public void setManualEventEnabled(boolean manualEventEnabled) {
		isManualEventEnabled = manualEventEnabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AgentNode agentNode = (AgentNode) o;
		return name.equals(agentNode.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

package com.gui.gui.panels;

import static com.gui.gui.panels.domain.PanelConstants.DETAILS_PANEL_ATTRIBUTES;
import static com.gui.gui.panels.domain.PanelConstants.DETAILS_PANEL_COMBO_BOX_ATTRIBUTES;
import static com.gui.gui.panels.domain.PanelConstants.DETAILS_PANEL_DEFAULT_PANEL_LAYOUT;
import static com.gui.gui.panels.domain.PanelConstants.DETAILS_PANEL_INFORMATION_PANEL_IDX;
import static com.gui.gui.panels.domain.PanelConstants.DETAILS_PANEL_TITLE;
import static com.gui.gui.panels.domain.PanelConstants.IS_NETWORK_AGENT;
import static com.gui.gui.utils.GUIComponentUtils.createDefaultAgentComboBox;
import static com.gui.gui.utils.GUIComponentUtils.createDefaultAgentComboBoxModel;
import static com.gui.gui.utils.GUILabelUtils.addPanelHeader;
import static com.gui.gui.utils.GUIPanelUtils.createBorderPanel;
import static com.gui.gui.utils.GUIPanelUtils.createDefaultEmptyPanel;
import static com.gui.gui.utils.GUIPanelUtils.createVerticallyScrolledPanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.gui.agents.domain.AgentNode;
import com.gui.domain.nodes.ClientAgentNode;

/**
 * Panel displaying detailed data regarding agents
 */
public class DetailsPanel {

	private static final JPanel DEFAULT_AGENT_PANEL = createDefaultEmptyPanel();

	private final JPanel mainPanel;
	private final JComboBox comboBoxNetworkAgents;
	private final JComboBox comboBoxClientAgents;
	private List<AgentNode> allNetworkAgentNodes;
	private List<AgentNode> allClientAgentNodes;
	private JPanel currentAgentPanel;

	/**
	 * Default constructor
	 */
	public DetailsPanel() {
		this.allNetworkAgentNodes = new ArrayList<>();
		this.allClientAgentNodes = new ArrayList<>();
		this.currentAgentPanel = DEFAULT_AGENT_PANEL;
		this.comboBoxNetworkAgents = createDefaultAgentComboBox(allNetworkAgentNodes, e -> changeSelectedAgent(false));
		this.comboBoxClientAgents = createDefaultAgentComboBox(allClientAgentNodes, e -> changeSelectedAgent(true));
		this.mainPanel = createDetailsPanel();
	}

	/**
	 * Method creates an initial details panel
	 *
	 * @return JPanel being details panel
	 */
	public JPanel createDetailsPanel() {
		final JPanel detailsPanel = createBorderPanel(DETAILS_PANEL_DEFAULT_PANEL_LAYOUT);
		addPanelHeader(DETAILS_PANEL_TITLE, detailsPanel);
		detailsPanel.add(comboBoxNetworkAgents, DETAILS_PANEL_COMBO_BOX_ATTRIBUTES);
		detailsPanel.add(comboBoxClientAgents, DETAILS_PANEL_COMBO_BOX_ATTRIBUTES);
		detailsPanel.add(createVerticallyScrolledPanel(currentAgentPanel), DETAILS_PANEL_ATTRIBUTES);
		return detailsPanel;
	}

	/**
	 * Method updates the network drop-down with new agent nodes
	 *
	 * @param agentNodes     list of node that has changed in combo box
	 * @param isClientUpdate flag indicating if the change was announced because of client agent
	 */
	public void revalidateComboBoxModel(final List<AgentNode> agentNodes, final boolean isClientUpdate) {
		if (!isClientUpdate) {
			allNetworkAgentNodes = agentNodes.stream().filter(IS_NETWORK_AGENT).toList();
			createDefaultAgentComboBoxModel(allNetworkAgentNodes, comboBoxNetworkAgents);
		} else {
			allClientAgentNodes = agentNodes.stream().filter(ClientAgentNode.class::isInstance).toList();
			createDefaultAgentComboBoxModel(allClientAgentNodes, comboBoxClientAgents);
		}
	}

	/**
	 * @return main panel
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}

	private void changeSelectedAgent(final boolean isClientComboBox) {
		final boolean isValid = isClientComboBox ?
				!(comboBoxNetworkAgents.getSelectedIndex() != 0 && comboBoxClientAgents.getSelectedIndex() == 0) :
				!(comboBoxClientAgents.getSelectedIndex() != 0 && comboBoxNetworkAgents.getSelectedIndex() == 0);

		if (isValid) {
			final List<AgentNode> agentNodesToTraverse = isClientComboBox ? allClientAgentNodes : allNetworkAgentNodes;
			final JComboBox comboBoxToUpdate = isClientComboBox ? comboBoxClientAgents : comboBoxNetworkAgents;
			final String newAgentName = (String) comboBoxToUpdate.getSelectedItem();

			currentAgentPanel = agentNodesToTraverse.stream()
					.filter(agent -> agent.getAgentName().equals(newAgentName))
					.findFirst()
					.map(AgentNode::getAgentDetailsPanel)
					.orElse(DEFAULT_AGENT_PANEL);
			refreshDetailsPanel();
			comboBoxToUpdate.setSelectedIndex(0);
		}
	}

	private void refreshDetailsPanel() {
		mainPanel.remove(DETAILS_PANEL_INFORMATION_PANEL_IDX);
		mainPanel.add(createVerticallyScrolledPanel(currentAgentPanel), DETAILS_PANEL_ATTRIBUTES);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
}

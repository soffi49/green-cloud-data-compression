package com.gui.domain.guielements;

import static com.gui.utils.GUIUtils.addPanelHeader;
import static com.gui.utils.GUIUtils.createBorderPanel;
import static com.gui.utils.GUIUtils.createDefaultComboBox;
import static com.gui.utils.GUIUtils.createDefaultScrollPane;
import static com.gui.utils.domain.StyleConstants.GRAY_1_COLOR;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.gui.domain.nodes.AgentNode;
import com.gui.domain.nodes.ClientAgentNode;
import com.gui.domain.nodes.MonitoringAgentNode;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;


/**
 * Class represents the details' panel of the GUI
 * <p>
 * {@value TITLE_LABEL}          title of the details panel
 */
public class DetailsPanel {

	private static final String TITLE_LABEL = "AGENT STATISTICS";
	private static final JPanel DEFAULT_INFO_PANEL = createDefaultMessagePanel();
	private static final CC DETAIL_PANEL_STYLE = new CC().height("100%").span().grow().wrap().gapY("5px", "0px");

	private static final int INFORMATION_PANEL_IDX = 3;
	private final JPanel detailPanel;
	private final JComboBox comboBoxNetwork;
	private final JComboBox comboBoxClients;
	private List<AgentNode> allNetworkAgentNodes;
	private List<AgentNode> allClientNodes;
	private JPanel agentDetailsPanel;

	/**
	 * Class constructor
	 */
	public DetailsPanel() {
		this.allNetworkAgentNodes = new ArrayList<>();
		this.allClientNodes = new ArrayList<>();
		this.agentDetailsPanel = DEFAULT_INFO_PANEL;
		this.comboBoxNetwork = initializeNetworkComboBox();
		this.comboBoxClients = initializeClientsComboBox();
		this.detailPanel = createDetailsPanel();
	}

	private static JPanel createDefaultMessagePanel() {
		final JPanel jPanel = new JPanel(new MigLayout(new LC().fillX().height("50px")));
		jPanel.setBackground(GRAY_1_COLOR);
		return jPanel;
	}

	/**
	 * Method creates an initial details panel
	 *
	 * @return JPanel being details panel
	 */
	public JPanel createDetailsPanel() {
		final MigLayout panelLayout = new MigLayout(new LC().fillX());
		final JPanel detailsPanel = createBorderPanel(panelLayout);
		addPanelHeader(TITLE_LABEL, detailsPanel);
		detailsPanel.add(comboBoxNetwork, new CC().height("20px").width("100%").wrap());
		detailsPanel.add(comboBoxClients, new CC().height("20px").width("100%").wrap());
		detailsPanel.add(initializeDetailPanelScroll(), DETAIL_PANEL_STYLE);
		return detailsPanel;
	}

	/**
	 * Method updates the network drop-down with new agent nodes
	 */
	public void revalidateNetworkComboBoxModel(final List<AgentNode> agentNodes) {
		allNetworkAgentNodes = agentNodes.stream().filter(agentNode -> !(agentNode instanceof MonitoringAgentNode)
				&& !(agentNode instanceof ClientAgentNode)).toList();
		comboBoxNetwork.setModel(new DefaultComboBoxModel(getDropDownNetworkAgentsNames()));
	}

	/**
	 * Method updates the network drop-down with new agent nodes
	 */
	public void revalidateClientComboBoxModel(final List<AgentNode> agentNodes) {
		allClientNodes = agentNodes.stream().filter(ClientAgentNode.class::isInstance).toList();
		comboBoxClients.setModel(new DefaultComboBoxModel(getDropDownClientAgentsNames()));
	}

	/**
	 * @return details panel
	 */
	public JPanel getDetailPanel() {
		return detailPanel;
	}

	private JComboBox initializeNetworkComboBox() {
		final JComboBox jComboBox = createDefaultComboBox(getDropDownNetworkAgentsNames());
		jComboBox.addActionListener(e -> changeSelectedNetworkAgent((String) jComboBox.getSelectedItem()));
		return jComboBox;
	}

	private JScrollPane initializeDetailPanelScroll() {
		final JScrollPane jScrollPane = createDefaultScrollPane(agentDetailsPanel);
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return jScrollPane;
	}

	private JComboBox initializeClientsComboBox() {
		final JComboBox jComboBox = createDefaultComboBox(getDropDownClientAgentsNames());
		jComboBox.addActionListener(e -> changeSelectedClientAgent((String) jComboBox.getSelectedItem()));
		return jComboBox;
	}

	private String[] getDropDownNetworkAgentsNames() {
		final List<String> agentNames = new java.util.ArrayList<>(
				allNetworkAgentNodes.stream().map(AgentNode::getName).toList());
		agentNames.add(0, "Please select the agent");
		return agentNames.toArray(new String[0]);
	}

	private String[] getDropDownClientAgentsNames() {
		final List<String> agentNames = new java.util.ArrayList<>(
				allClientNodes.stream().map(AgentNode::getName).toList());
		agentNames.add(0, "Please select the client");
		return agentNames.toArray(new String[0]);
	}

	private void changeSelectedNetworkAgent(final String newAgentName) {
		if (!(comboBoxClients.getSelectedIndex() != 0 && comboBoxNetwork.getSelectedIndex() == 0)) {
			agentDetailsPanel = allNetworkAgentNodes.stream()
					.filter(agent -> agent.getName().equals(newAgentName))
					.findFirst()
					.map(AgentNode::getInformationPanel)
					.orElse(DEFAULT_INFO_PANEL);
			refreshDetailsPanel();
			comboBoxClients.setSelectedIndex(0);
		}
	}

	private void changeSelectedClientAgent(final String newAgentName) {
		if (!(comboBoxNetwork.getSelectedIndex() != 0 && comboBoxClients.getSelectedIndex() == 0)) {
			agentDetailsPanel = allClientNodes.stream()
					.filter(agent -> agent.getName().equals(newAgentName))
					.findFirst()
					.map(AgentNode::getInformationPanel)
					.orElse(DEFAULT_INFO_PANEL);
			refreshDetailsPanel();
			comboBoxNetwork.setSelectedIndex(0);
		}
	}

	private void refreshDetailsPanel() {
		detailPanel.remove(INFORMATION_PANEL_IDX);
		detailPanel.add(initializeDetailPanelScroll(), DETAIL_PANEL_STYLE);
		detailPanel.revalidate();
		detailPanel.repaint();
	}
}

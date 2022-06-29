package com.gui.domain.guielements;

import static com.gui.utils.GUIUtils.createDefaultSubPanel;
import static com.gui.utils.GUIUtils.createJLabel;
import static com.gui.utils.domain.CommonConstants.DETAIL_PANEL;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.nodes.AgentNode;
import com.gui.domain.nodes.MonitoringAgentNode;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Class represents the details' panel of the GUI
 * <p>
 * {@value TITLE_LABEL}          title of the details panel
 */
public class DetailsPanel {

    private static final String TITLE_LABEL = "SELECT AGENT TO VIEW DETAILS";
    private static final JPanel DEFAULT_INFO_PANEL = createDefaultMessagePanel();

    private static final int INFORMATION_PANEL_IDX = 2;
    private final List<AgentNode> allNetworkAgentNodes;
    private final JPanel detailPanel;
    private final JComboBox comboBox;
    private JPanel agentDetailsPanel;

    /**
     * Class constructor
     *
     * @param agentNodes all agents nodes included in the select
     */
    public DetailsPanel(final List<AgentNode> agentNodes) {
        this.allNetworkAgentNodes = agentNodes;
        this.agentDetailsPanel = DEFAULT_INFO_PANEL;
        this.comboBox = initializeComboBox();
        this.detailPanel = createDetailsPanel();
    }

    private static JPanel createDefaultMessagePanel() {
        final JPanel jPanel = createDefaultSubPanel("NO_SELECTED_AGENT_PANEL", new MigLayout(new LC().fill()));
        jPanel.add(createJLabel(TITLE_FONT, BLUE_COLOR, "NO AGENT SELECTED"), new CC().spanX().spanY());
        return jPanel;
    }

    /**
     * Method creates an initial details panel
     *
     * @return JPanel being details panel
     */
    public JPanel createDetailsPanel() {
        final MigLayout panelLayout = new MigLayout(new LC().fillX());
        final JPanel detailsPanel = createDefaultSubPanel(DETAIL_PANEL, panelLayout);
        detailsPanel.add(createJLabel(FIRST_HEADER_FONT, DARK_BLUE_COLOR, TITLE_LABEL), new CC().spanX().gapY("0", "7px"));
        detailsPanel.add(comboBox, new CC().height("25px").growX().spanX());
        detailsPanel.add(agentDetailsPanel, new CC().height("25px").growX().spanX());
        return detailsPanel;
    }

    /**
     * Method updates the drop-down with new agent nodes
     */
    public void revalidateComboBoxModel() {
        comboBox.setModel(new DefaultComboBoxModel(getDropDownNetworkAgentsNames()));
    }

    /**
     * @return details panel
     */
    public JPanel getDetailPanel() {
        return detailPanel;
    }

    private JComboBox initializeComboBox() {
        final JComboBox jComboBox = new JComboBox(new DefaultComboBoxModel(getDropDownNetworkAgentsNames()));
        jComboBox.addActionListener(e -> changeSelectedAgent((String) jComboBox.getSelectedItem()));
        jComboBox.setBackground(Color.WHITE);
        jComboBox.setForeground(DARK_BLUE_COLOR);
        jComboBox.setFont(SECOND_HEADER_FONT);
        return jComboBox;
    }

    private String[] getDropDownNetworkAgentsNames() {
        final List<String> agentNames = new java.util.ArrayList<>(allNetworkAgentNodes.stream()
                                                                          .filter(agentNode -> !(agentNode instanceof MonitoringAgentNode))
                                                                          .map(AgentNode::getName)
                                                                          .toList());
        agentNames.add(0, "Please select the agent");
        return agentNames.toArray(new String[0]);
    }

    private void changeSelectedAgent(final String newAgentName) {
        agentDetailsPanel = allNetworkAgentNodes.stream()
                .filter(agent -> agent.getName().equals(newAgentName))
                .findFirst()
                .map(AgentNode::getInformationPanel)
                .orElse(DEFAULT_INFO_PANEL);
        refreshDetailsPanel();
    }

    private void refreshDetailsPanel() {
        detailPanel.remove(INFORMATION_PANEL_IDX);
        detailPanel.add(agentDetailsPanel, new CC().height("25px").growX().spanX());
        detailPanel.revalidate();
        detailPanel.repaint();
    }
}

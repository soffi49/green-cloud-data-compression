package com.gui.domain.guielements;

import static com.gui.domain.types.EventTypeEnum.POWER_SHORTAGE;
import static com.gui.utils.GUIUtils.*;
import static com.gui.utils.domain.StyleConstants.LIGHT_GRAY_COLOR;

import com.gui.domain.event.PowerShortageEvent;
import com.gui.domain.nodes.AgentNode;
import com.gui.domain.nodes.ClientAgentNode;
import com.gui.domain.nodes.MonitoringAgentNode;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel containing utilities which allow to invoke events in the simulation
 * <p>
 * TITLE_LABEL - text being the panel header
 * MAKE_POWER_SHORTAGE_BUTTON - label of the button for making power shortage
 * MAX_POWER - label describing power shortage input field
 * INITIAL_AGENT - initially selected agent
 */
public class AdminControlPanel {

    private static final String TITLE_LABEL = "ADMINISTRATOR TOOLS";
    private static final String MAKE_POWER_SHORTAGE_BUTTON = "MAKE POWER SHORTAGE";
    private static final String MAX_POWER = "MAX POWER";
    private static final String INITIAL_AGENT = "";


    private final JFormattedTextField powerShortageInput;
    private final JButton powerShortageButton;
    private final JComboBox comboBoxNetwork;
    private final JPanel adminControlPanel;
    private List<AgentNode> allNetworkAgentNodes;
    private String selectedAgent;


    /**
     * Class constructor
     */
    public AdminControlPanel() {
        this.allNetworkAgentNodes = new ArrayList<>();
        this.selectedAgent = INITIAL_AGENT;
        this.comboBoxNetwork = initializeNetworkComboBox();
        this.powerShortageButton = createButton(MAKE_POWER_SHORTAGE_BUTTON, event -> causePowerShortage());
        this.powerShortageInput = createNumericTextField(MAX_POWER);
        this.adminControlPanel = initializeAdminPanel();
    }

    /**
     * @return administrator panel
     */
    public JPanel getAdminControlPanel() {
        return adminControlPanel;
    }

    /**
     * Method updates the network drop-down with new agent nodes
     */
    public void revalidateNetworkComboBoxModel(final List<AgentNode> agentNodes) {
        allNetworkAgentNodes = agentNodes.stream().filter(agentNode -> !(agentNode instanceof MonitoringAgentNode) && !(agentNode instanceof ClientAgentNode)).toList();
        comboBoxNetwork.setModel(new DefaultComboBoxModel(getDropDownNetworkAgentsNames()));
    }

    private JPanel initializeAdminPanel() {
        final MigLayout panelLayout = new MigLayout(new LC().fillX().wrapAfter(2));
        final JPanel detailsPanel = createBorderPanel(panelLayout);
        initializePanelTop(detailsPanel);
        addOptionRow(detailsPanel, powerShortageButton, powerShortageInput);
        return detailsPanel;
    }

    private void initializePanelTop(final JPanel panel) {
        addPanelHeader(TITLE_LABEL, panel);
        panel.add(comboBoxNetwork, new CC().height("30px").growX().spanX());
        panel.add(createSeparator(LIGHT_GRAY_COLOR), new CC().spanX().growX());
    }

    /**
     * Note for the developers: to add your custom button invoking specific action, use this
     * method in the initializeAdminPanel function
     *
     * @param panel  panel to which the row should be added
     * @param button button which will fire the event
     * @param input  input that the user should provide
     */
    private void addOptionRow(final JPanel panel, final JButton button, final JFormattedTextField input) {
        panel.add(input, new CC().height("20px").growY().width("30%").alignY("center").gapX("10px", "20px"));
        panel.add(button, new CC().height("20px").grow().width("70%").alignY("center").gapX("0", "15px"));
        panel.add(createSeparator(LIGHT_GRAY_COLOR), new CC().spanX().growX());
    }

    private void causePowerShortage() {
        if (!powerShortageInput.getText().equals("")) {
            allNetworkAgentNodes.stream()
                    .filter(agentNode -> agentNode.getName().equals(selectedAgent))
                    .forEach(agentNode -> {
                        final int maxPower = Integer.parseInt(powerShortageInput.getText());
                        agentNode.setEvent(new PowerShortageEvent(POWER_SHORTAGE, getTimeForEventOccurrence(), maxPower));
                    });
        }
    }

    private OffsetDateTime getTimeForEventOccurrence() {
        return OffsetDateTime.now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime().plusSeconds(1);
    }

    private JComboBox initializeNetworkComboBox() {
        final JComboBox jComboBox = createDefaultComboBox(getDropDownNetworkAgentsNames());
        jComboBox.addActionListener(e -> changeSelectedNetworkAgent((String) jComboBox.getSelectedItem()));
        return jComboBox;
    }

    private String[] getDropDownNetworkAgentsNames() {
        final List<String> agentNames = new java.util.ArrayList<>(allNetworkAgentNodes.stream().map(AgentNode::getName).toList());
        agentNames.add(0, "Please select the agent");
        return agentNames.toArray(new String[0]);
    }

    private void changeSelectedNetworkAgent(final String newAgentName) {
        selectedAgent = comboBoxNetwork.getSelectedIndex() == 0 ? INITIAL_AGENT : newAgentName;
    }
}

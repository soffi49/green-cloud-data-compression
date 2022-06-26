package com.gui.domain.nodes;

import static com.gui.utils.domain.StyleConstants.MONITORING_STYLE;

/**
 * Agent node class representing the monitoring agent
 */
public class MonitoringAgentNode extends AgentNode {

    private String greenEnergyAgent;

    public MonitoringAgentNode(String name) {
        super(name);
        this.style = MONITORING_STYLE;
    }

    public String getGreenEnergyAgent() {
        return greenEnergyAgent;
    }

    public void setGreenEnergyAgent(String greenEnergyAgent) {
        this.greenEnergyAgent = greenEnergyAgent;
    }
}

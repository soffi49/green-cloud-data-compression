package com.gui.domain;

import static com.gui.domain.StyleUtils.GREEN_ENERGY_STYLE;
import static com.gui.domain.StyleUtils.MONITORING_STYLE;

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

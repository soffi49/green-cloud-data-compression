package com.gui.domain;

import static com.gui.domain.StyleUtils.GREEN_ENERGY_STYLE;

public class GreenEnergyAgentNode extends AgentNode {

    private String monitoringAgent;
    private String serverAgent;

    public GreenEnergyAgentNode(String name) {
        super(name);
        this.style = GREEN_ENERGY_STYLE;
    }

    public String getMonitoringAgent() {
        return monitoringAgent;
    }

    public void setMonitoringAgent(String monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    public String getServerAgent() {
        return serverAgent;
    }

    public void setServerAgent(String serverAgent) {
        this.serverAgent = serverAgent;
    }
}

package com.gui.domain;


import static com.gui.domain.StyleUtils.GREEN_ENERGY_STYLE;
import static com.gui.domain.StyleUtils.SERVER_STYLE;

import java.util.List;

public class ServerAgentNode extends AgentNode {
    private String cloudNetworkAgent;
    private List<String> greenEnergyAgents;
    private Coordinates location;

    public ServerAgentNode(String name) {
        super(name);
        this.style = SERVER_STYLE;
        this.coordinates = new Coordinates(0,0);
    }

    public String getCloudNetworkAgent() {
        return cloudNetworkAgent;
    }

    public void setCloudNetworkAgent(String cloudNetworkAgent) {
        this.cloudNetworkAgent = cloudNetworkAgent;
    }

    public List<String> getGreenEnergyAgents() {
        return greenEnergyAgents;
    }

    public void setGreenEnergyAgents(List<String> greenEnergyAgents) {
        this.greenEnergyAgents = greenEnergyAgents;
    }

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }
}

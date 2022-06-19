package com.gui.domain;

import static com.gui.domain.StyleUtils.CLIENT_STYLE;
import static com.gui.domain.StyleUtils.CLOUD_NETWORK_STYLE;

import java.util.ArrayList;
import java.util.List;

public class CloudNetworkAgentNode extends AgentNode {

    private List<String> serverAgents;

    public CloudNetworkAgentNode(String name) {
        super(name);
        this.style = CLOUD_NETWORK_STYLE;
        this.serverAgents = new ArrayList<>();
        this.coordinates = new Coordinates(0,0);
    }

    public List<String> getServerAgents() {
        return serverAgents;
    }

    public void setServerAgents(List<String> serverAgents) {
        this.serverAgents = serverAgents;
    }
}

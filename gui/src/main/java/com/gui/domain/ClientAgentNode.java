package com.gui.domain;

import static com.gui.domain.StyleUtils.CLIENT_STYLE;

public class ClientAgentNode extends AgentNode {

    public ClientAgentNode(String name, int id) {
        super(name);
        this.style = CLIENT_STYLE;
        this.coordinates = new Coordinates(0,2 * id);
    }
}

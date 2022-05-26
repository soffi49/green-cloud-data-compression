package com.gui;

import jade.core.Agent;
import java.util.Set;
import javafx.fxml.FXML;

public class AgentsFXController {

    Set<Agent> agents;

    @FXML
    protected void initialize() {
        agents = null;
    }
}
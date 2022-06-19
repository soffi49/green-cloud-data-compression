package com.gui.domain;

import static com.gui.domain.StyleUtils.*;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.Objects;

public class AgentNode {

    protected String name;
    protected String style;
    protected Coordinates coordinates;

    public AgentNode(String name) {
        this.name = name;
    }

    public Node addToGraph(final Graph graph){
        final Node node = graph.addNode(name);
        node.setAttribute("ui.label", node.getId());
        node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style)));
        node.setAttribute("xy", coordinates.getX(), coordinates.getY());
        return node;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentNode agentNode = (AgentNode) o;
        return name.equals(agentNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

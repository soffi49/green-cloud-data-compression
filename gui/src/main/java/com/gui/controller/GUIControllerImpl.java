package com.gui.controller;

import static com.gui.utils.GUIUtils.createCardShadow;
import static com.gui.utils.GUIUtils.createSeparator;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.guielements.DetailsPanel;
import com.gui.domain.guielements.InformationPanel;
import com.gui.domain.guielements.SummaryPanel;
import com.gui.domain.nodes.AgentNode;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


/**
 * {@value STYLE_FILE}      url for the file containing graph styling
 * {@value GUI_FRAME_SIZE}  multiplier describing the percentage of window's size which the GUI will take
 */
public class GUIControllerImpl implements GUIController {

    private static final String STYLE_FILE = "url(graphStyle.css)";
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final double GUI_FRAME_SIZE = 0.9;

    private final InformationPanel informationPanel;
    private final SummaryPanel summaryPanel;
    private final DetailsPanel detailsPanel;
    private final List<AgentNode> graphNodes;
    private JPanel mainPanel;
    private JFrame mainFrame;
    private Graph graph;

    public GUIControllerImpl() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graphNodes = new ArrayList<>();
        this.summaryPanel = new SummaryPanel();
        this.informationPanel = new InformationPanel();
        this.detailsPanel = new DetailsPanel(graphNodes);
        createGraph();
        createMainPanel();
        createMainFrame();
    }

    @Override
    public void createGUI() {
        mainFrame.setVisible(true);
    }

    @Override
    public synchronized void addAgentNodeToGraph(final AgentNode agent) {
        graphNodes.add(agent);
        agent.addToGraph(graph);
        detailsPanel.revalidateComboBoxModel();
    }

    @Override
    public synchronized void createEdges() {
        graphNodes.forEach(node -> node.createEdges(graph));
    }

    @Override
    public synchronized void removeAgentNodeFromGraph(final AgentNode agentNode) {
        graphNodes.remove(agentNode);
        graph.removeNode(agentNode.getName());
        detailsPanel.revalidateComboBoxModel();
    }

    @Override
    public synchronized void updateClientsCountByValue(int value) {
        summaryPanel.updateClientsCountByValue(value);
        refreshMainFrame();
    }

    @Override
    public synchronized void updateActiveJobsCountByValue(int value) {
        summaryPanel.updateActiveJobsCountByValue(value);
        refreshMainFrame();
    }

    @Override
    public synchronized void updateAllJobsCountByValue(int value) {
        summaryPanel.updateAllJobsCountByValue(value);
        refreshMainFrame();
    }

    @Override
    public synchronized void addNewInformation(String information) {
        informationPanel.addNewInformation(information);
        refreshMainFrame();
    }

    @Override
    public synchronized void displayMessageArrow(final AgentNode senderAgent, final List<String> receiversNames) {
        final List<Edge> edgesToDisplay = senderAgent.getEdges().stream()
                .filter(edge -> edge.isDirected() && receiversNames.contains(edge.getTargetNode().getId()))
                .toList();
        edgesToDisplay.forEach(edge -> edge.setAttribute("ui.class", EDGE_MESSAGE_STYLE));

        final ActionListener hideMessageArrowAction = e -> edgesToDisplay.forEach(edge -> edge.setAttribute("ui.class", EDGE_HIDDEN_MESSAGE_STYLE));
        final Timer hideMessageArrowTimer = new Timer(900, hideMessageArrowAction);
        hideMessageArrowTimer.start();
    }

    private void createMainFrame() {
        mainFrame = new JFrame("CLOUD NETWORK");
        mainFrame.setPreferredSize(new Dimension((int) (SCREEN_SIZE.width * GUI_FRAME_SIZE), (int) (SCREEN_SIZE.height * GUI_FRAME_SIZE)));
        mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    private void createMainPanel() {
        mainPanel = new JPanel();
        final MigLayout panelLayout = new MigLayout(new LC().wrapAfter(4));
        mainPanel.setLayout(panelLayout);
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createTitleLabel(), new CC().height("25px").gapAfter("5px").growX().spanX());
        mainPanel.add(createSeparator(BLUE_COLOR), new CC().growX().spanX());
        mainPanel.add(summaryPanel.getMainPanel(), new CC().height("30%").width("20%"));
        mainPanel.add(createGraphView(), new CC().height("100%").width("60%").grow().span(2, 2));
        mainPanel.add(detailsPanel.getDetailPanel(), new CC().height("80%").width("20%").alignY("center").spanY(2).wrap());
        mainPanel.add(informationPanel.getMainPanel(), new CC().height("70%").width("20%"));
    }

    private JLabel createTitleLabel() {
        final JLabel titleLabel = new JLabel("GREEN CLOUD NETWORK");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DARK_BLUE_COLOR);
        titleLabel.setBackground(Color.WHITE);
        titleLabel.setOpaque(true);
        return titleLabel;
    }

    private void createGraph() {
        graph = new MultiGraph("Cloud Network");
        graph.setAttribute("ui.stylesheet", STYLE_FILE);
        graph.setAttribute("layout.quality", "2");
        graph.setAttribute("ui.antialias");
    }

    private ViewPanel createGraphView() {
        final SwingViewer graphViewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        graphViewer.enableAutoLayout();
        final ViewPanel graphViewPanel = (ViewPanel) graphViewer.addDefaultView(false);
        graphViewPanel.setBorder(createCardShadow());
        return graphViewPanel;
    }

    private void refreshMainFrame() {
        mainFrame.revalidate();
        mainFrame.repaint();
    }
}

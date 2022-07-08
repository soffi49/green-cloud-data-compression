package com.gui.controller;

import static com.gui.utils.GUIUtils.*;
import static com.gui.utils.domain.StyleConstants.*;

import com.gui.domain.guielements.AdminControlPanel;
import com.gui.domain.guielements.DetailsPanel;
import com.gui.domain.guielements.InformationPanel;
import com.gui.domain.guielements.SummaryPanel;
import com.gui.domain.nodes.AgentNode;
import com.gui.domain.nodes.ClientAgentNode;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class GUIControllerImpl implements GUIController {

    private static final Logger logger = LoggerFactory.getLogger(GUIControllerImpl.class);

    private static final Dimension MAIN_SIZE = new Dimension((int) (SCREEN_SIZE.width * GUI_FRAME_SIZE_WIDTH) + SCROLL_BAR_WIDTH,
                                                             (int) (SCREEN_SIZE.height * GUI_FRAME_SIZE_HEIGHT) + SCROLL_BAR_WIDTH);

    private final InformationPanel informationPanel;
    private final SummaryPanel summaryPanel;
    private final DetailsPanel detailsPanel;
    private final AdminControlPanel adminControlPanel;
    private final List<AgentNode> graphNodes;
    private JScrollPane mainPanelScroll;
    private JFrame mainFrame;
    private JFrame adminFrame;
    private Graph graph;

    public GUIControllerImpl() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graphNodes = new ArrayList<>();
        this.summaryPanel = new SummaryPanel();
        this.informationPanel = new InformationPanel();
        this.detailsPanel = new DetailsPanel();
        this.adminControlPanel = new AdminControlPanel();
        createGraph();
        createMainPanel();
        createMainFrame();
        createAdminFrame();
    }

    @Override
    public void createGUI() {
        mainFrame.setVisible(true);
        SwingUtilities.invokeLater(() -> adminFrame.setVisible(true));
    }

    @Override
    public synchronized void addAgentNodeToGraph(final AgentNode agent) {
        graphNodes.add(agent);
        agent.addToGraph(graph);
        if (!(agent instanceof ClientAgentNode)) {
            detailsPanel.revalidateNetworkComboBoxModel(graphNodes);
            adminControlPanel.revalidateNetworkComboBoxModel(graphNodes);
        } else {
            detailsPanel.revalidateClientComboBoxModel(graphNodes);
        }
    }

    @Override
    public synchronized void createEdges() {
        graphNodes.forEach(node -> node.createEdges(graph));
    }

    @Override
    public void removeAgentNodeFromGraph(final AgentNode agent) {
        try {
            graph.removeNode(agent.getName());
        } catch (ElementNotFoundException e) {
            logger.info("Agent node {} was already removed", agent.getName());
        }
        graphNodes.remove(agent);
        if (!(agent instanceof ClientAgentNode)) {
            detailsPanel.revalidateNetworkComboBoxModel(graphNodes);
            adminControlPanel.revalidateNetworkComboBoxModel(graphNodes);
        } else {
            detailsPanel.revalidateClientComboBoxModel(graphNodes);
        }
    }

    @Override
    public void updateClientsCountByValue(int value) {
        summaryPanel.updateClientsCount(value);
        refreshMainFrame();
    }

    @Override
    public void updateActiveJobsCountByValue(int value) {
        summaryPanel.updateActiveJobsCountByValue(value);
        refreshMainFrame();
    }

    @Override
    public void updateAllJobsCountByValue(int value) {
        summaryPanel.updateAllJobsCountByValue(value);
        refreshMainFrame();
    }

    @Override
    public synchronized void addNewInformation(String information) {
        informationPanel.addNewInformation(information);
        refreshMainFrame();
    }

    @Override
    public void displayMessageArrow(final AgentNode senderAgent, final List<String> receiversNames) {
        final List<Edge> edgesToDisplay = senderAgent.getEdges().stream()
                .filter(edge -> edge.isDirected() && receiversNames.contains(edge.getTargetNode().getId()))
                .toList();
        synchronized (graph) {
            edgesToDisplay.forEach(edge -> edge.setAttribute("ui.class", EDGE_MESSAGE_STYLE));
        }

        final ActionListener hideMessageArrowAction = e -> {
            synchronized (graph) {
                edgesToDisplay.forEach(edge -> edge.setAttribute("ui.class", EDGE_HIDDEN_MESSAGE_STYLE));
            }
        };
        final Timer hideMessageArrowTimer = new Timer(900, hideMessageArrowAction);
        hideMessageArrowTimer.start();
    }

    private void createAdminFrame() {
        adminFrame = new JFrame("ADMIN PANEL");
        adminFrame.setSize(new Dimension(MAIN_SIZE.width / 2, MAIN_SIZE.height / 2));
        adminFrame.setResizable(false);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.add(adminControlPanel.getAdminControlPanel());
        mainFrame.setLocationRelativeTo(null);
    }

    private void createMainFrame() {
        mainFrame = new JFrame("CLOUD NETWORK");
        mainFrame.setSize(MAIN_SIZE);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.getContentPane().add(mainPanelScroll);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    private void createMainPanel() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(MAIN_SIZE);
        final MigLayout panelLayout = new MigLayout(new LC().wrapAfter(3).gridGap("10px", "10px").insets("10px", "10px", "10px", "10px"));
        mainPanel.setLayout(panelLayout);
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createTitleLabel("GREEN CLOUD NETWORK"), new CC().height("10%").gapAfter("5px").growX().spanX());
        mainPanel.add(createNetworkDetailsPanel(), new CC().height("100%").width("35%").spanY());
        mainPanel.add(createGraphView(), new CC().height("70%").width("65%").grow().spanX(2).wrap());
        mainPanel.add(createInformationPanel(), new CC().height("30%").grow().spanX(2));
        mainPanelScroll = createDefaultScrollPane(mainPanel);
        mainPanelScroll.setPreferredSize(MAIN_SIZE);
    }

    private JPanel createNetworkDetailsPanel() {
        final JPanel networkDetailsPanel = createShadowPanel(new MigLayout(new LC().fill().wrapAfter(1)));
        networkDetailsPanel.add(summaryPanel.getMainPanel(), new CC().height("30%").spanX().grow().gapY("10px", "20px"));
        networkDetailsPanel.add(detailsPanel.getDetailPanel(), new CC().height("70%").spanX().grow().gapY("0px", "10px"));
        return networkDetailsPanel;
    }

    private JPanel createInformationPanel() {
        final JPanel networkDetailsPanel = createShadowPanel(new MigLayout(new LC().fill().wrapAfter(1)));
        networkDetailsPanel.add(informationPanel.getMainPanel(), new CC().spanX().grow());
        return networkDetailsPanel;
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
        synchronized (mainFrame) {
            mainFrame.revalidate();
            mainFrame.repaint();
            adminFrame.revalidate();
            adminFrame.repaint();
        }
    }
}

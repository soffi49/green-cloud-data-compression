package com.gui.controller;

import static com.gui.utils.GUIUtils.createDefaultScrollPane;
import static com.gui.utils.GUIUtils.createShadowPanel;
import static com.gui.utils.GUIUtils.createTitleLabel;
import static com.gui.utils.domain.StyleConstants.GUI_FRAME_SIZE_HEIGHT;
import static com.gui.utils.domain.StyleConstants.GUI_FRAME_SIZE_WIDTH;
import static com.gui.utils.domain.StyleConstants.GUI_SCROLL_BAR_WIDTH;
import static com.gui.utils.domain.StyleConstants.SCREEN_SIZE;

import com.gui.domain.guielements.AdminControlPanel;
import com.gui.domain.guielements.DetailsPanel;
import com.gui.domain.guielements.InformationPanel;
import com.gui.domain.guielements.SummaryPanel;
import com.gui.domain.nodes.AgentNode;
import com.gui.domain.nodes.ClientAgentNode;
import com.gui.graph.GraphService;
import com.gui.graph.GraphServiceImpl;
import com.mxgraph.swing.mxGraphComponent;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class GUIControllerImpl implements GUIController {
    private static final Dimension MAIN_SIZE = new Dimension(
            (int) (SCREEN_SIZE.width * GUI_FRAME_SIZE_WIDTH) + GUI_SCROLL_BAR_WIDTH,
            (int) (SCREEN_SIZE.height * GUI_FRAME_SIZE_HEIGHT) + GUI_SCROLL_BAR_WIDTH);

    private final InformationPanel informationPanel;
    private final SummaryPanel summaryPanel;
    private final DetailsPanel detailsPanel;
    private final AdminControlPanel adminControlPanel;
    private final List<AgentNode> graphNodes;
    private final mxGraphComponent graph;
    private final GraphService graphService;
    private JScrollPane mainPanelScroll;
    private JFrame mainFrame;
    private JFrame adminFrame;

    public GUIControllerImpl() {
        this.graphNodes = new ArrayList<>();
        this.summaryPanel = new SummaryPanel();
        this.informationPanel = new InformationPanel();
        this.detailsPanel = new DetailsPanel();
        this.adminControlPanel = new AdminControlPanel();
        this.graphService = new GraphServiceImpl(new Dimension((int) (MAIN_SIZE.width * 0.65), (int) (MAIN_SIZE.height * 0.7)));
        graph = graphService.createGraphComponent();
        createMainPanel();
        createMainFrame();
        createAdminFrame();
    }

    @Override
    public void run() {
        mainFrame.setVisible(true);
        SwingUtilities.invokeLater(() -> adminFrame.setVisible(true));
    }

    @Override
    public synchronized void addAgentNodeToGraph(final AgentNode agent) {
        graphNodes.add(agent);
        agent.addToGraph(graphService);
        if (!(agent instanceof ClientAgentNode)) {
            detailsPanel.revalidateNetworkComboBoxModel(graphNodes);
            adminControlPanel.revalidateNetworkComboBoxModel(graphNodes);
        } else {
            detailsPanel.revalidateClientComboBoxModel(graphNodes);
        }
    }

    @Override
    public synchronized void createEdges() {
        graphNodes.forEach(AgentNode::createEdges);
        graphService.updateGraphLayout();
    }

    @Override
    public void removeAgentNodeFromGraph(final AgentNode agent) {
        graphService.removeNodeFromGraph(agent);
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
        graphService.displayMessageEdges(senderAgent.getName(), receiversNames);
    }

    private void createAdminFrame() {
        adminFrame = new JFrame("ADMIN PANEL");
        adminFrame.setSize(new Dimension(MAIN_SIZE.width / 2, MAIN_SIZE.height / 2));
        adminFrame.setResizable(false);
        adminFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        adminFrame.add(adminControlPanel.getAdminControlPanel());
        mainFrame.setLocationRelativeTo(null);
    }

    private void createMainFrame() {
        mainFrame = new JFrame("CLOUD NETWORK");
        mainFrame.setSize(MAIN_SIZE);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainFrame.getContentPane().add(mainPanelScroll);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    private void createMainPanel() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(MAIN_SIZE);
        final MigLayout panelLayout = new MigLayout(
                new LC().wrapAfter(3).gridGap("10px", "10px").insets("10px", "10px", "10px", "10px"));
        mainPanel.setLayout(panelLayout);
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createTitleLabel("GREEN CLOUD NETWORK"), new CC().height("10%").gapAfter("5px").growX().spanX());
        mainPanel.add(createNetworkDetailsPanel(), new CC().height("100%").width("35%").spanY());
        mainPanel.add(graph, new CC().height("70%").width("65%").grow().spanX(2).wrap());
        mainPanel.add(createInformationPanel(), new CC().height("30%").grow().spanX(2));
        mainPanelScroll = createDefaultScrollPane(mainPanel);
        mainPanelScroll.setPreferredSize(MAIN_SIZE);
    }

    private JPanel createNetworkDetailsPanel() {
        final JPanel networkDetailsPanel = createShadowPanel(new MigLayout(new LC().fill().wrapAfter(1)));
        networkDetailsPanel.add(summaryPanel.getMainPanel(),
                                new CC().height("30%").spanX().grow().gapY("10px", "20px"));
        networkDetailsPanel.add(detailsPanel.getDetailPanel(),
                                new CC().height("70%").spanX().grow().gapY("0px", "10px"));
        return networkDetailsPanel;
    }

    private JPanel createInformationPanel() {
        final JPanel networkDetailsPanel = createShadowPanel(new MigLayout(new LC().fill().wrapAfter(1)));
        networkDetailsPanel.add(informationPanel.getMainPanel(), new CC().spanX().grow());
        return networkDetailsPanel;
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

package com.gui.domain.guielements;

import static com.gui.utils.domain.CommonConstants.SUMMARY_PANEL;
import static com.gui.utils.StyleUtils.*;
import static com.gui.utils.domain.StyleConstants.*;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Class represents the summary panel of the GUI
 * <p>
 * {@value TITLE_LABEL}         title of the summary panel
 * {@value CLIENT_COUNT_LABEL}  label for the number of cloud network clients
 * {@value ACTIVE_JOBS_LABEL}   label for the number of currently running jobs
 * {@value ALL_JOBS_COUNT}      label for the number of all booked (currently running + planned) jobs
 */
public class SummaryPanel {

    private static final String TITLE_LABEL = "NETWORK STATISTICS";
    private static final String CLIENT_COUNT_LABEL = "NUMBER OF CLIENTS:";
    private static final String ACTIVE_JOBS_LABEL = "NUMBER OF CURRENTLY RUNNING JOBS:";
    private static final String ALL_JOBS_COUNT = "NUMBER OF ALL PLANNED JOBS:";

    private final JPanel mainPanel;
    private JLabel clientsCountNumberLabel;
    private JLabel activeJobsCountNumberLabel;
    private JLabel allJobsCountNumberLabel;

    private int clientsCount;
    private int activeJobsCount;
    private int allJobsCount;


    /**
     * Default constructor
     */
    public SummaryPanel() {
        this.clientsCount = 0;
        this.activeJobsCount = 0;
        this.allJobsCount = 0;
        initializeNumberLabels();
        this.mainPanel = createSummaryPanel();
    }

    /**
     * Class constructor
     *
     * @param clientsCount    number of clients of cloud network
     * @param activeJobsCount number of jobs being currently run
     * @param allJobsCount    number of all (currently running + planned) jobs
     */
    public SummaryPanel(int clientsCount, int activeJobsCount, int allJobsCount) {
        this.clientsCount = clientsCount;
        this.activeJobsCount = activeJobsCount;
        this.allJobsCount = allJobsCount;
        initializeNumberLabels();
        this.mainPanel = createSummaryPanel();
    }

    /**
     * Function created the summary panel
     *
     * @return JPanel being the summary panel
     */
    public JPanel createSummaryPanel() {
        final MigLayout panelLayout = new MigLayout(new LC().gridGapX("15px"));
        final JPanel summaryPanel = new JPanel();
        summaryPanel.setName(SUMMARY_PANEL);
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setLayout(panelLayout);
        summaryPanel.setBorder(createCardShadow());

        createHeaderSection(summaryPanel);
        createBodySection(summaryPanel);

        return summaryPanel;
    }

    /**
     * Function updates the clients number by given value
     *
     * @param value value to be added to the clients number
     */
    public void updateClientsCountByValue(final int value) {
        clientsCount += value;
        clientsCountNumberLabel.setText(String.valueOf(clientsCount));
    }

    /**
     * Function updates the active jobs number by given value
     *
     * @param value value to be added to the active jobs number
     */
    public void updateActiveJobsCountByValue(final int value) {
        activeJobsCount += value;
        activeJobsCountNumberLabel.setText(String.valueOf(activeJobsCount));
    }

    /**
     * Function updates the all jobs number by given value
     *
     * @param value value to be added to the all jobs number
     */
    public void updateAllJobsCountByValue(final int value) {
        allJobsCount += value;
        allJobsCountNumberLabel.setText(String.valueOf(allJobsCount));
    }

    /**
     * @return summary panel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createHeaderSection(final JPanel panel) {
        final JLabel titleLabel = new JLabel(TITLE_LABEL);
        titleLabel.setFont(FIRST_HEADER_FONT);
        titleLabel.setForeground(DARK_BLUE_COLOR);
        panel.add(titleLabel, new CC().spanX().gapY("0", "7px"));
    }

    private void createBodySection(final JPanel panel) {
        final List<Pair<String, JLabel>> summaryPanelLabels = List.of(new Pair<>(formatToHTML(CLIENT_COUNT_LABEL), clientsCountNumberLabel),
                                                                      new Pair<>(formatToHTML(ACTIVE_JOBS_LABEL), activeJobsCountNumberLabel),
                                                                      new Pair<>(formatToHTML(ALL_JOBS_COUNT), allJobsCountNumberLabel));

        panel.add(createSeparator(LIGHT_GRAY_COLOR), new CC().spanX().growX().wrap());
        summaryPanelLabels.forEach(pair -> {
            final JLabel headerLabel = new JLabel(pair.getFirst());
            headerLabel.setFont(DESCRIPTION_FONT);
            headerLabel.setForeground(BLUE_COLOR);

            panel.add(headerLabel, new CC().span(2));
            panel.add(pair.getSecond(), new CC().wrap());
            panel.add(createSeparator(LIGHT_GRAY_COLOR), new CC().spanX().growX().wrap().gapY("2px", "5px"));
        });
    }

    private void initializeNumberLabels() {
        this.clientsCountNumberLabel = new JLabel(String.valueOf(clientsCount));
        this.activeJobsCountNumberLabel = new JLabel(String.valueOf(activeJobsCount));
        this.allJobsCountNumberLabel = new JLabel(String.valueOf(allJobsCount));

        clientsCountNumberLabel.setFont(SECOND_HEADER_FONT);
        clientsCountNumberLabel.setForeground(BLUE_COLOR);
        activeJobsCountNumberLabel.setFont(SECOND_HEADER_FONT);
        activeJobsCountNumberLabel.setForeground(BLUE_COLOR);
        allJobsCountNumberLabel.setFont(SECOND_HEADER_FONT);
        allJobsCountNumberLabel.setForeground(BLUE_COLOR);
    }
}

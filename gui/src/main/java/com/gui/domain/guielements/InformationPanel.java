package com.gui.domain.guielements;

import static com.gui.utils.StyleUtils.*;
import static com.gui.utils.domain.CommonConstants.INFORMATION_PANEL;
import static com.gui.utils.domain.StyleConstants.*;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Class represents the information panel of the GUI
 */
public class InformationPanel {

    private static final String TITLE_LABEL = "LATEST NEWS";

    private final JPanel informationBoxPanel;
    private JScrollPane informationBoxScroll;
    private final JPanel mainPanel;
    private final List<Pair<String, String>> informationList;

    /**
     * Default constructor
     */
    public InformationPanel() {
        this.informationList = new ArrayList<>();
        this.informationBoxPanel = new JPanel();
        this.informationBoxScroll = new JScrollPane(informationBoxPanel);
        this.mainPanel = createInformationPanel();
    }

    /**
     * Class constructor
     *
     * @param informationList list of information to be displayed in panel
     */
    public InformationPanel(List<Pair<String, String>> informationList) {
        this.informationList = informationList;
        this.informationBoxPanel = new JPanel();
        this.informationBoxScroll = new JScrollPane(informationBoxPanel);
        this.mainPanel = createInformationPanel();
    }

    /**
     * Function creates the information panel
     *
     * @return JPanel being the information panel
     */
    public JPanel createInformationPanel() {
        final MigLayout panelLayout = new MigLayout(new LC().fillX());
        final JPanel informationMainPanel = new JPanel();
        informationMainPanel.setName(INFORMATION_PANEL);
        informationMainPanel.setBackground(Color.WHITE);
        informationMainPanel.setLayout(panelLayout);
        informationMainPanel.setBorder(createCardShadow());

        createTitleSection(informationMainPanel);
        createBodySection();

        informationBoxScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        informationBoxScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        informationBoxScroll.getVerticalScrollBar().setValue(informationBoxScroll.getVerticalScrollBar().getMaximum());

        informationMainPanel.add(informationBoxScroll, new CC().height("100%").span().grow().wrap());
        informationMainPanel.add(createSeparator(DARK_BLUE_COLOR), new CC().spanX().growX().wrap());

        return informationMainPanel;
    }

    /**
     * Function adds new information to information box panel
     *
     * @param information information that is to be added
     */
    public void addNewInformation(final String information) {
        if (informationList.size() >= 20) {
            informationList.remove(0);
        }
        informationList.add(new Pair<>(information, getCurrentTime()));
        createBodySection();
    }

    /**
     * @return information panel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createTitleSection(final JPanel panel) {
        final JLabel titleLabel = new JLabel(TITLE_LABEL);
        titleLabel.setFont(FIRST_HEADER_FONT);
        titleLabel.setForeground(DARK_BLUE_COLOR);

        panel.add(titleLabel, new CC().height("20px").spanX());
        panel.add(createSeparator(DARK_BLUE_COLOR), new CC().spanX().growX().wrap());
    }

    private void createBodySection() {
        informationBoxPanel.removeAll();
        final MigLayout layout = new MigLayout(new LC().bottomToTop().fillX().flowY());
        informationBoxPanel.setBackground(Color.WHITE);
        informationBoxPanel.setLayout(layout);

        final int lastIdx = informationList.size();

        if (!informationList.isEmpty()) {
            IntStream.range(0, lastIdx).forEach(idx -> {
                final Pair<String, String> information = informationList.get(lastIdx - 1 - idx);
                informationBoxPanel.add(createInformationLabel(Optional.ofNullable(information)), new CC().spanX().growX());
            });
        } else {
            informationBoxPanel.add(createInformationLabel(Optional.empty()), new CC().spanX().growX());
        }
        informationBoxScroll.getVerticalScrollBar().setValue(informationBoxScroll.getVerticalScrollBar().getMaximum());
    }

    private JLabel createInformationLabel(final Optional<Pair<String, String>> information) {
        final JLabel infoLabel = information
                .map(pair -> new JLabel(String.format("[%s]: %s", pair.getSecond(), pair.getFirst())))
                .orElseGet(() -> new JLabel(String.format("[%s]: %s", getCurrentTime(), "There are no latest news")));
        infoLabel.setFont(DESCRIPTION_FONT);
        infoLabel.setForeground(BLUE_COLOR);
        return infoLabel;
    }

    private String getCurrentTime() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return formatter.format(LocalDateTime.now());
    }
}

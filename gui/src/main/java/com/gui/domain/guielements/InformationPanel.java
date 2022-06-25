package com.gui.domain.guielements;

import static com.gui.utils.StyleUtils.*;
import static com.gui.utils.domain.CommonConstants.INFORMATION_PANEL;
import static com.gui.utils.domain.StyleConstants.*;

import net.miginfocom.layout.AC;
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
    private static final Font NEWS_FONT = DESCRIPTION_FONT;
    private static final double NEWS_GRID_GAP = 5;
    private static final int MAX_NEWS_ROW_HEIGHT = (int) (getFontPixels(NEWS_FONT) + 2 * NEWS_GRID_GAP);

    private final JPanel informationBoxPanel;
    private final JPanel mainPanel;
    private final List<Pair<String, String>> informationList;

    /**
     * Default constructor
     */
    public InformationPanel() {
        this.informationList = new ArrayList<>();
        this.informationBoxPanel = new JPanel();
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

        informationMainPanel.add(informationBoxPanel, new CC().height("100%").span().grow().wrap());
        informationMainPanel.add(createSeparator(DARK_BLUE_COLOR), new CC().spanX().growX().wrap());

        return informationMainPanel;
    }

    /**
     * Function adds new information to information box panel
     *
     * @param information information that is to be added
     */
    public void addNewInformation(final String information) {
        if (informationList.size() >= Math.floor((double) informationBoxPanel.getHeight() / MAX_NEWS_ROW_HEIGHT) - 2) {
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
        final String size = String.format("%dpx:%dpx:%dpx", MAX_NEWS_ROW_HEIGHT, MAX_NEWS_ROW_HEIGHT, MAX_NEWS_ROW_HEIGHT);
        final MigLayout layout = new MigLayout(new LC().bottomToTop().fillX().flowY(), null, new AC().size(size));
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
    }

    private JLabel createInformationLabel(final Optional<Pair<String, String>> information) {
        final JLabel infoLabel = information
                .map(pair -> new JLabel(String.format("[%s] %s", pair.getSecond(), pair.getFirst())))
                .orElseGet(() -> new JLabel(String.format("[%s] %s", getCurrentTime(),"There are no latest news")));
        infoLabel.setFont(NEWS_FONT);
        infoLabel.setForeground(BLUE_COLOR);
        return infoLabel;
    }

    private String getCurrentTime() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return formatter.format(LocalDateTime.now());
    }
}

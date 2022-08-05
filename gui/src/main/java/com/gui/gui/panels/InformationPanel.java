package com.gui.gui.panels;

import static com.gui.gui.panels.domain.PanelConstants.INFORMATION_PANEL_INFO_BOX_ATTRIBUTES;
import static com.gui.gui.panels.domain.PanelConstants.INFORMATION_PANEL_INFO_BOX_LAYOUT;
import static com.gui.gui.panels.domain.PanelConstants.INFORMATION_PANEL_MAIN_LAYOUT;
import static com.gui.gui.panels.domain.PanelConstants.INFORMATION_PANEL_ROW_ATTRIBUTES;
import static com.gui.gui.panels.domain.PanelConstants.INFORMATION_PANEL_TITLE;
import static com.gui.gui.utils.GUIComponentUtils.createSeparator;
import static com.gui.gui.utils.GUILabelUtils.addPanelHeader;
import static com.gui.gui.utils.GUILabelUtils.createParagraphLabel;
import static com.gui.gui.utils.GUIContainerUtils.createBorderPanel;
import static com.gui.gui.utils.GUIContainerUtils.createDefaultScrollPanel;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_2_COLOR;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.math3.util.Pair;

import net.miginfocom.layout.CC;

/**
 * Panel displaying information box containing last news about cloud network
 */
public class InformationPanel {

	private final JPanel mainPanel;
	private final JPanel informationBoxPanel;
	private final List<Pair<String, String>> informationList;
	private final JScrollPane informationBoxScroll;

	/**
	 * Default constructor
	 */
	public InformationPanel() {
		this.informationList = new ArrayList<>();
		this.informationBoxPanel = initializeInformationBoxPanel();
		this.informationBoxScroll = initializeInformationBoxScroll();
		this.mainPanel = initializeMainPanel();
	}

	/**
	 * Function adds new information to information box panel
	 *
	 * @param information information that is to be added
	 */
	public void addNewInformation(final String information) {
		synchronized (informationList) {
			if (informationList.size() >= 50) {
				informationList.remove(0);
			}
		}
		synchronized (informationList) {
			informationList.add(new Pair<>(information, getCurrentTime()));
		}
		createBodySection();
	}

	/**
	 * @return information panel
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}

	private JPanel initializeInformationBoxPanel() {
		final JPanel infoBoxPanel = new JPanel();
		infoBoxPanel.setBackground(Color.WHITE);
		infoBoxPanel.setLayout(INFORMATION_PANEL_INFO_BOX_LAYOUT);
		return infoBoxPanel;
	}

	private JScrollPane initializeInformationBoxScroll() {
		final JScrollPane infoBoxScroll = createDefaultScrollPanel(informationBoxPanel);
		infoBoxScroll.getVerticalScrollBar().setValue(infoBoxScroll.getVerticalScrollBar().getMaximum());
		return infoBoxScroll;
	}

	private JPanel initializeMainPanel() {
		final JPanel informationMainPanel = createBorderPanel(INFORMATION_PANEL_MAIN_LAYOUT);
		addPanelHeader(INFORMATION_PANEL_TITLE, informationMainPanel);
		createBodySection();
		informationMainPanel.add(informationBoxScroll, INFORMATION_PANEL_INFO_BOX_ATTRIBUTES);
		return informationMainPanel;
	}

	private void createBodySection() {
		informationBoxPanel.removeAll();
		final int lastIdx = informationList.size();

		if (!informationList.isEmpty()) {
			IntStream.range(0, lastIdx).forEach(idx -> {
				final Pair<String, String> information = informationList.get(lastIdx - 1 - idx);
				informationBoxPanel.add(createSeparator(GRAY_2_COLOR), new CC().spanX().growX());
				informationBoxPanel.add(createInformationLabel(information), INFORMATION_PANEL_ROW_ATTRIBUTES);
			});
		} else {
			informationBoxPanel.add(createInformationLabel(null), INFORMATION_PANEL_ROW_ATTRIBUTES);
		}
		informationBoxScroll.getVerticalScrollBar().setValue(informationBoxScroll.getVerticalScrollBar().getMaximum());
	}

	private JLabel createInformationLabel(final Pair<String, String> information) {
		final String labelText = Objects.nonNull(information) ?
				String.format("[%s]: %s", information.getSecond(), information.getFirst()) :
				String.format("[%s]: %s", getCurrentTime(), "There are no latest news");
		return createParagraphLabel(labelText);
	}

	private String getCurrentTime() {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		return formatter.format(LocalDateTime.now());
	}
}

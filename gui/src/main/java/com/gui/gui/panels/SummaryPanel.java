package com.gui.gui.panels;

import static com.gui.gui.panels.domain.PanelConstants.SUMMARY_PANEL_MAIN_LAYOUT;
import static com.gui.gui.panels.domain.PanelConstants.SUMMARY_PANEL_TITLE;
import static com.gui.gui.panels.domain.listlabels.SummaryPanelListLabelEnum.ACTIVE_JOBS_LABEL;
import static com.gui.gui.panels.domain.listlabels.SummaryPanelListLabelEnum.ALL_JOBS_COUNT;
import static com.gui.gui.panels.domain.listlabels.SummaryPanelListLabelEnum.CLIENT_COUNT_LABEL;
import static com.gui.gui.utils.GUILabelUtils.addPanelHeader;
import static com.gui.gui.utils.GUILabelUtils.createListLabel;
import static com.gui.gui.utils.GUILabelUtils.formatToHTML;
import static com.gui.gui.utils.GUIContainerUtils.createBorderPanel;
import static com.gui.gui.utils.GUIContainerUtils.createLabelListPanel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gui.gui.panels.domain.listlabels.ListLabelEnum;

import net.miginfocom.layout.CC;

/**
 * Panel displaying summarized data about entire cloud network
 */
public class SummaryPanel {

	private final JPanel mainPanel;
	private final Map<ListLabelEnum, JLabel> labelMap;
	private final AtomicInteger clientsCount;
	private final AtomicInteger activeJobsCount;
	private final AtomicInteger allJobsCount;

	/**
	 * Default constructor
	 */
	public SummaryPanel() {
		this.clientsCount = new AtomicInteger(0);
		this.activeJobsCount = new AtomicInteger(0);
		this.allJobsCount = new AtomicInteger(0);
		this.labelMap = initializeLabels();
		this.mainPanel = initializeSummaryPanel();
	}

	/**
	 * Function updates the clients number by given value
	 *
	 * @param value value to be added to client count
	 */
	public void updateClientsCount(final int value) {
		clientsCount.getAndAdd(value);
		labelMap.get(CLIENT_COUNT_LABEL).setText(formatToHTML(formatToHTML(String.valueOf(clientsCount))));
	}

	/**
	 * Function updates the active jobs number by given value
	 *
	 * @param value value to be added to the active jobs number
	 */
	public void updateActiveJobsCountByValue(final int value) {
		activeJobsCount.getAndAdd(value);
		labelMap.get(ACTIVE_JOBS_LABEL).setText(formatToHTML(String.valueOf(activeJobsCount)));
	}

	/**
	 * Function updates the all jobs number by given value
	 *
	 * @param value value to be added to the all jobs number
	 */
	public void updateAllJobsCountByValue(final int value) {
		allJobsCount.getAndAdd(value);
		labelMap.get(ALL_JOBS_COUNT).setText(formatToHTML(String.valueOf(allJobsCount)));
	}

	/**
	 * @return summary panel
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel initializeSummaryPanel() {
		final JPanel panel = createBorderPanel(SUMMARY_PANEL_MAIN_LAYOUT);
		addPanelHeader(SUMMARY_PANEL_TITLE, panel);
		panel.add(createLabelListPanel(labelMap), new CC().grow());
		return panel;
	}

	private Map<ListLabelEnum, JLabel> initializeLabels() {
		final Map<ListLabelEnum, JLabel> jLabelMap = new LinkedHashMap<>();
		jLabelMap.put(CLIENT_COUNT_LABEL, createListLabel(String.valueOf(clientsCount)));
		jLabelMap.put(ACTIVE_JOBS_LABEL, createListLabel(String.valueOf(activeJobsCount)));
		jLabelMap.put(ALL_JOBS_COUNT, createListLabel(String.valueOf(allJobsCount)));
		return jLabelMap;
	}
}

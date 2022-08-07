package com.gui.gui.panels.domain.listlabels;

import static com.gui.gui.utils.GUILabelUtils.formatToHTML;

/**
 * Enum storing all available summary panel label types
 */
public enum SummaryPanelListLabelEnum implements ListLabelEnum {

	CLIENT_COUNT_LABEL("NUMBER OF CLIENTS:"),
	ACTIVE_JOBS_LABEL("NUMBER OF CURRENTLY RUNNING JOBS:"),
	ALL_JOBS_COUNT("NUMBER OF ALL PLANNED JOBS:");

	private final String label;

	SummaryPanelListLabelEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return formatToHTML(label);
	}
}

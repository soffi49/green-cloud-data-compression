package com.gui.gui.panels.domain;

import java.util.function.Predicate;

import com.gui.agents.domain.AgentNode;
import com.gui.domain.nodes.ClientAgentNode;
import com.gui.domain.nodes.MonitoringAgentNode;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Class storing all constants connected with panels used in GUI
 */
public class PanelConstants {

	/**
	 * Common constants
	 */
	public static final Predicate<AgentNode> IS_NETWORK_AGENT =
			agentNode -> !(agentNode instanceof MonitoringAgentNode) && !(agentNode instanceof ClientAgentNode);

	/**
	 * Panel titles
	 */
	public static final String ADMIN_PANEL_TITLE = "ADMINISTRATOR TOOLS";
	public static final String DETAILS_PANEL_TITLE = "AGENT STATISTICS";
	public static final String INFORMATION_PANEL_TITLE = "LATEST NEWS";
	public static final String SUMMARY_PANEL_TITLE = "NETWORK STATISTICS";

	/**
	 * Admin panel constants
	 */
	public static final MigLayout ADMIN_PANEL_DEFAULT_PANEL_LAYOUT = new MigLayout(new LC().fillX());
	public static final String ADMIN_PANEL_DEFAULT_EMPTY_PANEL_TITLE = "DEFAULT_EMPTY_PANEL";
	public static final CC ADMIN_PANEL_EVENT_PANEL_ATTRIBUTES = new CC().growX().spanX().wrap();
	public static final CC ADMIN_PANEL_COMBO_BOX_ATTRIBUTES = new CC().height("30px").grow().spanX();
	public static final CC ADMIN_PANEL_SEPARATOR_ATTRIBUTES = new CC().spanX().growX();

	/**
	 * Details panel constants
	 */
	public static final MigLayout DETAILS_PANEL_DEFAULT_PANEL_LAYOUT = new MigLayout(new LC().fillX());
	public static final CC DETAILS_PANEL_ATTRIBUTES = new CC().height("100%").span().grow().wrap().gapY("5px", "0px");
	public static final CC DETAILS_PANEL_COMBO_BOX_ATTRIBUTES = new CC().height("20px").width("100%").wrap();
	public static final int DETAILS_PANEL_INFORMATION_PANEL_IDX = 3;

	/**
	 * Information panel constants
	 */
	public static final MigLayout INFORMATION_PANEL_MAIN_LAYOUT = new MigLayout(new LC().fillX());
	public static final MigLayout INFORMATION_PANEL_INFO_BOX_LAYOUT = new MigLayout(
			new LC().bottomToTop().fillX().flowY());
	public static final CC INFORMATION_PANEL_ROW_ATTRIBUTES = new CC().spanX().growX();
	public static final CC INFORMATION_PANEL_INFO_BOX_ATTRIBUTES = new CC().height("100%").span().grow().wrap()
			.gapY("5px", "0px");

	/**
	 * Summary panel constants
	 */
	public static final MigLayout SUMMARY_PANEL_MAIN_LAYOUT = new MigLayout(new LC().fillX().gridGapX("15px"));
}

package com.gui.controller.domain;

import static com.gui.gui.utils.domain.GUIStyleConstants.GUI_FRAME_SIZE_HEIGHT;
import static com.gui.gui.utils.domain.GUIStyleConstants.GUI_FRAME_SIZE_WIDTH;
import static com.gui.gui.utils.domain.GUIStyleConstants.GUI_SCROLL_BAR_WIDTH;
import static com.gui.gui.utils.domain.GUIStyleConstants.SCREEN_SIZE;

import java.awt.Dimension;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Class storing all constants connected with main GUI controller
 */
public class GUIControllerConstants {

	/**
	 * Controller titles
	 */
	public static final String ADMIN_PANEL_TITLE = "ADMIN PANEL";
	public static final String USER_PANEL_TITLE = "CLOUD NETWORK";
	public static final String MAIN_PANEL_TITLE = "GREEN CLOUD NETWORK";

	/**
	 * Controller sizes
	 */
	public static final Dimension MAIN_SIZE = new Dimension(
			(int) (SCREEN_SIZE.width * GUI_FRAME_SIZE_WIDTH) + GUI_SCROLL_BAR_WIDTH,
			(int) (SCREEN_SIZE.height * GUI_FRAME_SIZE_HEIGHT) + GUI_SCROLL_BAR_WIDTH);
	public static final Dimension GRAPH_DIMENSIONS = new Dimension((int) (MAIN_SIZE.width * 0.65),
			(int) (MAIN_SIZE.height * 0.7));
	public static final Dimension ADMIN_FRAME_SIZE = new Dimension(MAIN_SIZE.width / 2, MAIN_SIZE.height / 2);

	/**
	 * Controller layouts
	 */
	public static final MigLayout MAIN_PANEL_LAYOUT = new MigLayout(
			new LC().wrapAfter(3).gridGap("10px", "10px").insets("10px", "10px", "10px", "10px"));
	public static final MigLayout NETWORK_DETAIL_PANEL_LAYOUT = new MigLayout(new LC().fill().wrapAfter(1));
	public static final CC MAIN_PANEL_TITLE_ATTRIBUTES = new CC().height("10%").gapAfter("5px").growX().spanX();
	public static final CC NETWORK_DETAIL_PANEL_ATTRIBUTES = new CC().height("100%").width("35%").spanY();
	public static final CC GRAPH_PANEL_ATTRIBUTES =new CC().height("70%").width("65%").grow().spanX(2).wrap();
	public static final CC INFORMATION_PANEL_ATTRIBUTES = new CC().height("30%").grow().spanX(2);
	public static final CC SUMMARY_PANEL_ATTRIBUTES = new CC().height("30%").spanX().grow().gapY("10px", "20px");
	public static final CC AGENT_DETAILS_PANEL_ATTRIBUTES = new CC().height("70%").spanX().grow().gapY("0px", "10px");

}

package com.gui.utils.domain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

/**
 * Class stores constants used in styling the objects
 */
public class StyleConstants {

	/**
	 * Frame constants
	 */
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	public static final double GUI_FRAME_SIZE_WIDTH = 0.8;
	public static final double GUI_FRAME_SIZE_HEIGHT = 0.85;
	public static final int GUI_SCROLL_BAR_WIDTH = 10;

	/**
	 * Color palette constants
	 */
	public static final Color GREEN_1_COLOR = new Color(108, 189, 58);
	public static final Color GREEN_2_COLOR = new Color(78, 148, 24);
	public static final Color WHITE_COLOR = new Color(255, 255, 255);
	public static final Color GRAY_1_COLOR = new Color(245, 245, 245);
	public static final Color GRAY_2_COLOR = new Color(204, 204, 204);
	public static final Color GRAY_3_COLOR = new Color(162, 162, 162);
	public static final Color GRAY_4_COLOR = new Color(101, 101, 101);
	public static final Color GRAY_5_COLOR = new Color(95, 95, 95);
	public static final Color GRAY_6_COLOR = new Color(44, 44, 44);

	/**
	 * Font family constants
	 */
	public static final String FONT_FAMILY = "Dialog";

	/**
	 * Font size constants
	 */
	private static final int TITLE_FONT_SIZE_PROPORTION = 70;
	public static final int FONT_SIZE_1 = (int) SCREEN_SIZE.getWidth() / TITLE_FONT_SIZE_PROPORTION;
	/**
	 * Font type constants
	 */
	public static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_1);
	public static final int FONT_SIZE_2 = (int) SCREEN_SIZE.getWidth() / (TITLE_FONT_SIZE_PROPORTION + 35);
	public static final Font FIRST_HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_2);
	public static final int FONT_SIZE_3 = (int) SCREEN_SIZE.getWidth() / (TITLE_FONT_SIZE_PROPORTION + 45);
	public static final Font SECOND_HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_3);
	public static final int FONT_SIZE_4 = (int) SCREEN_SIZE.getWidth() / (TITLE_FONT_SIZE_PROPORTION + 55);
	public static final Font LABEL_FONT = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_4);
	public static final int FONT_SIZE_5 = (int) SCREEN_SIZE.getWidth() / (TITLE_FONT_SIZE_PROPORTION + 60);
	public static final Font DESCRIPTION_FONT = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_5);
	public static final int FONT_SIZE_6 = (int) SCREEN_SIZE.getWidth() / (TITLE_FONT_SIZE_PROPORTION + 80);
	public static final Font DEFAULT_FONT = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_6);

}

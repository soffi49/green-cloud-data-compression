package com.gui.gui.utils;

import static com.gui.gui.utils.domain.GUIStyleConstants.DEFAULT_FONT;
import static com.gui.gui.utils.domain.GUIStyleConstants.FIRST_HEADER_FONT;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_4_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_5_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GREEN_1_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.LABEL_FONT;
import static com.gui.gui.utils.domain.GUIStyleConstants.TITLE_FONT;
import static com.gui.gui.utils.domain.GUIStyleConstants.WHITE_COLOR;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.layout.CC;

/**
 * Class provides set of utilities connected with styling GUI label/text objects
 */
public class GUILabelUtils {

	/**
	 * Method formats the input string into a string enclosed inside html tags
	 *
	 * @param string string to be formatted
	 * @return string enclosed by html tags
	 */
	public static String formatToHTML(final String string) {
		return String.format("<html><span>%s</span></html>", string);
	}

	/**
	 * Method creates JLabel with given font style and foreground color
	 *
	 * @param font       font style for JLabel
	 * @param foreground font color for JLabel
	 * @param text       JLabel text
	 * @return styled JLabel
	 */
	public static JLabel createJLabel(final Font font, final Color foreground, final String text) {
		final JLabel jLabel = new JLabel(formatToHTML(text));
		jLabel.setFont(font);
		jLabel.setForeground(foreground);
		return jLabel;
	}

	/**
	 * Method creates list element which is JLabel
	 *
	 * @param text JLabel text
	 * @return list element JLabel
	 */
	public static JLabel createListLabel(final String text) {
		return createJLabel(LABEL_FONT, WHITE_COLOR, formatToHTML(text));
	}

	/**
	 * Method creates a paragraph which is a JLabel
	 *
	 * @param text JLabel text
	 * @return JLabel being a paragraph
	 */
	public static JLabel createParagraphLabel(final String text) {
		return createJLabel(DEFAULT_FONT, GRAY_5_COLOR, formatToHTML(text));
	}

	/**
	 * Method creates title label for main frame
	 *
	 * @param title text of the label
	 * @return JLabel being title label
	 */
	public static JLabel createTitleLabel(final String title) {
		final JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(WHITE_COLOR);
		titleLabel.setBackground(GREEN_1_COLOR);
		titleLabel.setOpaque(true);
		titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		return titleLabel;
	}

	/**
	 * Method adds the header for the panel
	 *
	 * @param text  header text
	 * @param panel panel to which the header is to be added
	 */
	public static void addPanelHeader(final String text, final JPanel panel) {
		panel.add(createJLabel(FIRST_HEADER_FONT, GRAY_4_COLOR, text), new CC().spanX().gapY("5px", "5px"));
	}

}

package com.gui.gui.utils;

import static com.gui.gui.utils.GUIComponentUtils.customizeScrollBar;
import static com.gui.gui.utils.GUILabelUtils.createJLabel;
import static com.gui.gui.utils.domain.GUIStyleConstants.DESCRIPTION_FONT;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_1_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_2_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_4_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GREEN_2_COLOR;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.jdesktop.swingx.border.DropShadowBorder;

import com.gui.gui.panels.domain.listlabels.ListLabelEnum;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Class provides set of utilities connected GUI container objects
 */
public class GUIContainerUtils {

	/**
	 * Method creates the shadow to be added to some JPanel
	 *
	 * @return border being a shadow
	 */
	private static Border createCardShadow() {
		final DropShadowBorder shadow = new DropShadowBorder();
		shadow.setShadowColor(Color.GRAY);
		shadow.setShowLeftShadow(true);
		shadow.setShowRightShadow(true);
		shadow.setShowBottomShadow(true);
		shadow.setShowTopShadow(true);
		return shadow;
	}

	/**
	 * Method creates panel with shadow
	 *
	 * @param layout layout of the panel
	 * @return styled JPanel
	 */
	public static JPanel createShadowPanel(final LayoutManager layout) {
		final JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(layout);
		panel.setBorder(createCardShadow());
		return panel;
	}

	/**
	 * Method creates panel with top and bottom border
	 *
	 * @param layout layout of the panel
	 * @return styled JPanel
	 */
	public static JPanel createBorderPanel(final LayoutManager layout) {
		final JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(layout);
		panel.setBorder(new MatteBorder(5, 0, 5, 0, GREEN_2_COLOR));
		return panel;
	}

	/**
	 * Method creates default empty panel
	 *
	 * @return styled JPanel
	 */
	public static JPanel createDefaultEmptyPanel() {
		final JPanel jPanel = new JPanel(new MigLayout(new LC().fill()));
		jPanel.setBackground(GRAY_1_COLOR);
		return jPanel;
	}

	/**
	 * Method creates the scroll pane
	 *
	 * @param panel panel inside scroll pane
	 * @return JScrollPane
	 */
	public static JScrollPane createDefaultScrollPanel(final JPanel panel) {
		final JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		customizeScrollBar(scrollPane.getVerticalScrollBar());
		customizeScrollBar(scrollPane.getHorizontalScrollBar());
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scrollPane;
	}

	/**
	 * Method creates vertically scrolled panel from given JPanel
	 *
	 * @param panel panel put inside the scrollbar
	 * @return styled JPanel
	 */
	public static JScrollPane createVerticallyScrolledPanel(final JPanel panel) {
		final JScrollPane jScrollPane = createDefaultScrollPanel(panel);
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return jScrollPane;
	}

	/**
	 * Method creates the panel displaying a list of labels together with their descriptions/values
	 *
	 * @param labelMap map containing set of values as JLabels together with their String descriptions/values
	 * @return JPanel being the list panel
	 */
	public static JPanel createLabelListPanel(final Map<ListLabelEnum, JLabel> labelMap) {
		final MigLayout layout = new MigLayout(new LC().fillX().gridGap("0px", "10px"));
		final JPanel panel = new JPanel(layout);
		panel.setBackground(Color.WHITE);
		labelMap.forEach((labelType, labelValue) -> {
			final JPanel valuePanel = new JPanel();
			valuePanel.setBackground(GRAY_4_COLOR);
			valuePanel.add(labelValue, new CC().spanX());
			labelValue.setBorder(new EmptyBorder(2, 2, 2, 2));
			final JPanel headerPanel = new JPanel();
			headerPanel.setBackground(GRAY_4_COLOR);
			final JLabel headerLabel = createJLabel(DESCRIPTION_FONT, GRAY_2_COLOR, labelType.getLabel());
			headerLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
			headerPanel.add(headerLabel, new CC().spanX());
			panel.add(headerPanel, new CC().spanX(3).grow());
			panel.add(valuePanel, new CC().spanX(2).grow().wrap());
		});
		return panel;
	}

	/**
	 * Method creates a default frame based on given by the user parameters
	 *
	 * @param title     title of the frame
	 * @param dimension size of the frame
	 * @param content   the main content of the frame
	 * @return JFrame
	 */
	public static JFrame createDefaultFrame(final String title, final Dimension dimension, final Component content) {
		final JFrame jFrame = new JFrame(title);
		jFrame.setSize(dimension);
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		jFrame.getContentPane().add(content);
		jFrame.setLocationRelativeTo(null);
		return jFrame;
	}
}

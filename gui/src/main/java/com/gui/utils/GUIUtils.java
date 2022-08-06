package com.gui.utils;

import static com.gui.utils.domain.StyleConstants.DEFAULT_FONT;
import static com.gui.utils.domain.StyleConstants.DESCRIPTION_FONT;
import static com.gui.utils.domain.StyleConstants.FIRST_HEADER_FONT;
import static com.gui.utils.domain.StyleConstants.GRAY_1_COLOR;
import static com.gui.utils.domain.StyleConstants.GRAY_2_COLOR;
import static com.gui.utils.domain.StyleConstants.GRAY_3_COLOR;
import static com.gui.utils.domain.StyleConstants.GRAY_4_COLOR;
import static com.gui.utils.domain.StyleConstants.GRAY_5_COLOR;
import static com.gui.utils.domain.StyleConstants.GREEN_1_COLOR;
import static com.gui.utils.domain.StyleConstants.GREEN_2_COLOR;
import static com.gui.utils.domain.StyleConstants.GUI_SCROLL_BAR_WIDTH;
import static com.gui.utils.domain.StyleConstants.LABEL_FONT;
import static com.gui.utils.domain.StyleConstants.SECOND_HEADER_FONT;
import static com.gui.utils.domain.StyleConstants.TITLE_FONT;
import static com.gui.utils.domain.StyleConstants.WHITE_COLOR;

import com.gui.domain.types.LabelEnum;
import com.gui.utils.domain.ComboBoxRenderer;
import com.gui.utils.domain.ComboBoxStyle;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Class provides set of utilities connected with GUI objects' styling or creation
 */
public class GUIUtils {

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
	 * Method creates the horizontal separator
	 *
	 * @param color color of the separator
	 * @return separator being JSeparator object
	 */
	public static JSeparator createSeparator(final Color color) {
		final JSeparator separator = new JSeparator();
		separator.setBackground(color);
		separator.setForeground(color);
		return separator;
	}

	/**
	 * Method creates the shadow to be added to some JPanel
	 *
	 * @return border being a shadow
	 */
	public static Border createCardShadow() {
		final DropShadowBorder shadow = new DropShadowBorder();
		shadow.setShadowColor(Color.GRAY);
		shadow.setShowLeftShadow(true);
		shadow.setShowRightShadow(true);
		shadow.setShowBottomShadow(true);
		shadow.setShowTopShadow(true);
		return shadow;
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
	public static JLabel createParagraph(final String text) {
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
	 * @return JLabel being the header
	 */
	public static void addPanelHeader(final String text, final JPanel panel) {
		panel.add(createJLabel(FIRST_HEADER_FONT, GRAY_4_COLOR, text), new CC().spanX().gapY("5px", "5px"));
	}

	/**
	 * Method adds the customized styling to scroll bars
	 *
	 * @param scrollBar scroll bar to style
	 */
	public static void customizeScrollBar(final JScrollBar scrollBar) {
		scrollBar.setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = GRAY_2_COLOR;
				this.trackColor = GRAY_1_COLOR;
				this.scrollBarWidth = GUI_SCROLL_BAR_WIDTH;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton jbutton = new JButton();
				jbutton.setPreferredSize(new Dimension(0, 0));
				jbutton.setMinimumSize(new Dimension(0, 0));
				jbutton.setMaximumSize(new Dimension(0, 0));
				return jbutton;
			}

		});
		scrollBar.setBackground(Color.WHITE);
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
	 * Method creates the panel displaying a list of labels together with their descriptions
	 *
	 * @param labelMap map containing set of values as JLabels together with their String descriptions
	 * @return JPanel being the list panel
	 */
	public static JPanel createLabelListPanel(final Map<LabelEnum, JLabel> labelMap) {
		final MigLayout layout = new MigLayout(new LC().fillX().gridGap("0px", "10px"));
		final JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(layout);
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
	 * Method creates the comboBox and initialize it with given values
	 *
	 * @param comboBoxValues values of the dropdown
	 * @return JComboBox
	 */
	public static JComboBox createDefaultComboBox(final String[] comboBoxValues) {
		final JComboBox jComboBox = new JComboBox(new DefaultComboBoxModel(comboBoxValues));
		jComboBox.setRenderer(new ComboBoxRenderer());
		jComboBox.setUI(ComboBoxStyle.createUI(jComboBox));
		final Border comboBoxBorder = BorderFactory.createLineBorder(GRAY_5_COLOR, 2);
		jComboBox.setBorder(comboBoxBorder);
		jComboBox.setForeground(GRAY_5_COLOR);
		return jComboBox;
	}

	/**
	 * Method creates the scroll pane
	 *
	 * @param panel panel inside scroll pane
	 * @return JScrollPane
	 */
	public static JScrollPane createDefaultScrollPane(final JPanel panel) {
		final JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		customizeScrollBar(scrollPane.getVerticalScrollBar());
		customizeScrollBar(scrollPane.getHorizontalScrollBar());
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scrollPane;
	}

	/**
	 * Method creates the numeric input field
	 *
	 * @param title input field title
	 * @return numeric input field
	 */
	public static JFormattedTextField createNumericTextField(final String title) {
		final JFormattedTextField jFormattedTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		jFormattedTextField.setForeground(GRAY_5_COLOR);
		jFormattedTextField.setFont(LABEL_FONT);
		jFormattedTextField.setText("0");
		jFormattedTextField.setMargin(new Insets(0, 10, 0, 10));
		final Border border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(GRAY_4_COLOR, 2),
				title,
				TitledBorder.LEFT,
				TitledBorder.TOP,
				LABEL_FONT);
		jFormattedTextField.setBorder(border);
		return jFormattedTextField;
	}

	/**
	 * Method creates a default button
	 *
	 * @param title          button text
	 * @param actionListener button action listeners
	 * @return JButton
	 */
	public static JButton createButton(final String title, final ActionListener actionListener) {
		final JButton jButton = new JButton();
		jButton.addActionListener(actionListener);
		makeButtonEnabled(jButton, title);
		return jButton;
	}

	/**
	 * Method styles the button as disabled
	 *
	 * @param jButton     button to style
	 * @param disableText text to be placed on the button
	 */
	public static void makeButtonDisabled(final JButton jButton, final String disableText) {
		jButton.setBorder(BorderFactory.createLineBorder(GRAY_3_COLOR));
		jButton.setFont(SECOND_HEADER_FONT);
		jButton.setBackground(GRAY_3_COLOR);
		jButton.setForeground(GRAY_2_COLOR);
		jButton.setText(disableText);
		jButton.setUI(new BasicButtonUI() {
			@Override
			protected void paintButtonPressed(Graphics g, AbstractButton b) {
				if (b.isContentAreaFilled()) {
					Dimension size = b.getSize();
					g.setColor(GRAY_3_COLOR);
					g.fillRect(0, 0, size.width, size.height);
				}
			}
		});
	}

	/**
	 * Method styles the button as enabled
	 *
	 * @param jButton     button to style
	 * @param enabledText text to be placed on the button
	 */
	public static void makeButtonEnabled(final JButton jButton, final String enabledText) {
		jButton.setBorder(BorderFactory.createLineBorder(GRAY_5_COLOR));
		jButton.setFont(SECOND_HEADER_FONT);
		jButton.setBackground(GRAY_5_COLOR);
		jButton.setForeground(GRAY_2_COLOR);
		jButton.setText(enabledText);
		jButton.setUI(new BasicButtonUI() {
			@Override
			protected void paintButtonPressed(Graphics g, AbstractButton b) {
				if (b.isContentAreaFilled()) {
					Dimension size = b.getSize();
					g.setColor(GRAY_2_COLOR);
					g.fillRect(0, 0, size.width, size.height);
				}
			}
		});
	}
}

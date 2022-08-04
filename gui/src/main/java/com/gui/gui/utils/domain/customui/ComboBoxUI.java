package com.gui.gui.utils.domain.customui;

import static com.gui.gui.utils.GUIComponentUtils.customizeScrollBar;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_5_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.WHITE_COLOR;

import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * Class overrides' combo box UI to assign custom styling
 */
public class ComboBoxUI extends BasicComboBoxUI {
	public ComboBoxUI() {
		currentValuePane.setBackground(WHITE_COLOR);
	}

	public static javax.swing.plaf.ComboBoxUI createUI() {
		return new ComboBoxUI();
	}

	@Override
	protected JButton createArrowButton() {
		final JButton button = new BasicArrowButton(SwingConstants.SOUTH, GRAY_5_COLOR, GRAY_5_COLOR, WHITE_COLOR,
				GRAY_5_COLOR);
		button.setBorder(BorderFactory.createLineBorder(GRAY_5_COLOR, 2));
		return button;
	}

	@Override
	protected ComboPopup createPopup() {
		return new BasicComboPopup(comboBox) {
			@Serial
			private static final long serialVersionUID = 440559789243251164L;

			@Override
			protected JScrollPane createScroller() {
				final JScrollPane jScrollPane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				customizeScrollBar(jScrollPane.getVerticalScrollBar());
				return jScrollPane;
			}
		};
	}
}
package com.gui.gui.utils.domain.customui;

import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_1_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_2_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.GUI_SCROLL_BAR_WIDTH;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Class overrides' scroll bar UI to assign custom styling
 */
public class ScrollBarUI extends BasicScrollBarUI {

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
}

package com.gui.utils.domain;

import static com.gui.utils.domain.StyleConstants.GRAY_5_COLOR;
import static com.gui.utils.domain.StyleConstants.LABEL_FONT;
import static com.gui.utils.domain.StyleConstants.WHITE_COLOR;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class ComboBoxRenderer extends DefaultListCellRenderer {

	private static final int MARGIN = 3;

	public ComboBoxRenderer() {
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		c.setBackground(WHITE_COLOR);
		c.setForeground(GRAY_5_COLOR);
		c.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		c.setFont(LABEL_FONT);
		return c;
	}

	@Override
	public Color getBackground() {
		return WHITE_COLOR;
	}

}

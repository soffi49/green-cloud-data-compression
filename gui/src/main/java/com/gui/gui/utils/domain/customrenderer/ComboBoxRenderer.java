package com.gui.gui.utils.domain.customrenderer;

import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_5_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.LABEL_FONT;
import static com.gui.gui.utils.domain.GUIStyleConstants.WHITE_COLOR;

import java.awt.Color;
import java.awt.Component;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Class extends list renderer to assign appropriate styling to combo box
 */
public class ComboBoxRenderer extends DefaultListCellRenderer {

	@Serial
	private static final long serialVersionUID = 7313195204073989516L;
	private static final int MARGIN = 3;

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

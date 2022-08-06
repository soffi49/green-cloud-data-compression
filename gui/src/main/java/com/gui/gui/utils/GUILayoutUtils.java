package com.gui.gui.utils;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JPanel;

/**
 * Class provides set of utilities connected with GUI layouts
 */
public class GUILayoutUtils {

	/**
	 * Method adds elements as header to JPanel which is using grid layout
	 *
	 * @param components list of components to be added
	 * @param panel      panel to which the components are to be added
	 */
	public static void addHeaderComponentsToGrid(final List<Component> components, final JPanel panel) {
		final GridBagLayout layout = (GridBagLayout) panel.getLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(5, 10, 5, 10);
		constraints.gridwidth = GridBagConstraints.REMAINDER;

		components.forEach(component -> addComponentToGrid(panel, component, layout, constraints));
	}

	/**
	 * Method adds elements to grid taking into account the proportion of the horizontal space that it can occupy
	 *
	 * @param component  component to be added
	 * @param panel      panel to which the component is to be added
	 * @param proportion proportion of the horizontal space that the element should take
	 * @param isRowEnd   flag indicating if the component is appended at the row end
	 */
	public static void addComponentToGridWithHorizontalProportion(final Component component, final JPanel panel,
			final double proportion, final boolean isRowEnd) {
		final GridBagLayout layout = (GridBagLayout) panel.getLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridwidth = isRowEnd ? GridBagConstraints.REMAINDER : GridBagConstraints.RELATIVE;
		constraints.weightx = proportion;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(10, 10, 10, 10);

		addComponentToGrid(panel, component, layout, constraints);
	}

	private static void addComponentToGrid(final JPanel panel, final Component component,
			final GridBagLayout gridBagLayout,
			final GridBagConstraints gridBagConstraints) {
		gridBagLayout.setConstraints(component, gridBagConstraints);
		panel.add(component);
	}
}

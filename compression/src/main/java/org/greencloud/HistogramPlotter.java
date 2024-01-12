package org.greencloud;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.statistics.HistogramDataset;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HistogramPlotter extends ApplicationFrame {
	public HistogramPlotter(String title, byte[] data) {
		super(title);

		// Create a histogram dataset
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("Histogram", toArray(data), 10); // 10 bins

		// Create the chart
		JFreeChart chart = ChartFactory.createHistogram(
				"Byte Array Histogram", // Chart title
				"Value",                // X-axis label
				"Frequency",            // Y-axis label
				dataset
		);

		// Create a chart panel and set its preferred size
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(560, 370));

		// Set up the application frame
		setContentPane(chartPanel);
		pack();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		setVisible(true);
	}

	// Convert byte array to double array
	private double[] toArray(byte[] data) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = data[i];
		}
		return result;
	}

}

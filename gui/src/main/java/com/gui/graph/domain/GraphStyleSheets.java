package com.gui.graph.domain;

import static com.gui.graph.domain.GraphStyleConstants.GREEN_ENERGY_NODE_SIZE;
import static com.gui.graph.domain.GraphStyleConstants.SERVER_NODE_SIZE;
import static com.gui.utils.domain.StyleConstants.GRAY_6_COLOR;
import static com.gui.utils.domain.StyleConstants.WHITE_COLOR;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;

import java.util.Hashtable;
import java.util.Map;

/**
 * Class defines all available style sheets
 */
public class GraphStyleSheets {

    // General stylesheets

    private static Map<String, Object> getLabelStyleSheet() {
        final Map<String, Object> labelStyleSheet = new Hashtable<>();
        labelStyleSheet.put(mxConstants.STYLE_FONTFAMILY, "Dialog");
        labelStyleSheet.put(mxConstants.STYLE_FONTSIZE, 8);
        labelStyleSheet.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        labelStyleSheet.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(GRAY_6_COLOR));
        labelStyleSheet.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, mxUtils.getHexColorString(WHITE_COLOR));
        labelStyleSheet.put(mxConstants.STYLE_LABEL_BORDERCOLOR, mxUtils.getHexColorString(GRAY_6_COLOR));
        labelStyleSheet.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
        labelStyleSheet.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_CENTER);
        labelStyleSheet.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER);
        labelStyleSheet.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        labelStyleSheet.put(mxConstants.STYLE_SPACING, 10);
        return labelStyleSheet;
    }

    // Cloud Network stylesheets

    private static Map<String, Object> getCloudNetworkStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_AUTOSIZE, 1);
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKEWIDTH, 2);
        nodeStyleSheet.putAll(getLabelStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the cloud network node when cloud network is inactive
     */
    public static Map<String, Object> getCloudNetworkInactiveStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#6c6c6c");
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#6c6c6c");
        nodeStyleSheet.putAll(getCloudNetworkStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the cloud network node when cloud network has low traffic
     */
    public static Map<String, Object> getCloudNetworkLowTrafficStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#6ec020");
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#6ec020");
        nodeStyleSheet.putAll(getCloudNetworkStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the cloud network node when cloud network has medium traffic
     */
    public static Map<String, Object> getCloudNetworkMediumTrafficStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#FBC028");
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#FBC028");
        nodeStyleSheet.putAll(getCloudNetworkStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the cloud network node when cloud network has high traffic
     */
    public static Map<String, Object> getCloudNetworkHighTrafficStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#F15524");
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#F15524");
        nodeStyleSheet.putAll(getCloudNetworkStyleSheet());
        return nodeStyleSheet;
    }

    // Server stylesheets

    private static Map<String, Object> getServerStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_AUTOSIZE, 1);
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
        nodeStyleSheet.put(mxConstants.STYLE_STROKEWIDTH, 3);
        nodeStyleSheet.putAll(getLabelStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the server node when server is inactive
     */
    public static Map<String, Object> getServerInactiveStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#B3B3B3");
        nodeStyleSheet.putAll(getServerStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the server node when server is active
     */
    public static Map<String, Object> getServerActiveStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#6ec020");
        nodeStyleSheet.putAll(getServerStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the server node when server has some jobs put on hold
     */
    public static Map<String, Object> getServerOnHoldStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_IMAGE_WIDTH, SERVER_NODE_SIZE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#f95300");
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        nodeStyleSheet.put(mxConstants.STYLE_IMAGE, "/icons/pauseIcon.png");
        nodeStyleSheet.putAll(getServerStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the server node when server has some jobs on back up power
     */
    public static Map<String, Object> getServerOnBackUpStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_IMAGE_WIDTH, SERVER_NODE_SIZE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#f95300");
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        nodeStyleSheet.put(mxConstants.STYLE_IMAGE, "/icons/noPowerIcon.png");
        nodeStyleSheet.putAll(getServerStyleSheet());
        return nodeStyleSheet;
    }

    // Green energy stylesheets

    private static Map<String, Object> getGreenEnergyStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_AUTOSIZE, 1);
        nodeStyleSheet.put(mxConstants.STYLE_STROKEWIDTH, 3);
        nodeStyleSheet.putAll(getLabelStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the green energy node when green energy source is inactive
     */
    public static Map<String, Object> getGreenEnergyInactiveStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#B3B3B3");
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#9d9d9d");
        nodeStyleSheet.putAll(getGreenEnergyStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the green energy node when green energy source is active
     */
    public static Map<String, Object> getGreenEnergyActiveStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#56a111");
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#6ec020");
        nodeStyleSheet.putAll(getGreenEnergyStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the green energy node when green energy source has some jobs put on hold
     */
    public static Map<String, Object> getGreenEnergyOnHoldStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_IMAGE_WIDTH, GREEN_ENERGY_NODE_SIZE);
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#f95300");
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
        nodeStyleSheet.put(mxConstants.STYLE_TARGET_PERIMETER_SPACING, -5);
        nodeStyleSheet.put(mxConstants.STYLE_IMAGE, "/icons/pauseIcon.png");
        nodeStyleSheet.putAll(getGreenEnergyStyleSheet());
        return nodeStyleSheet;
    }

    // Monitoring stylesheet

    /**
     * @return style sheet defined for the monitoring agent node
     */
    public static Map<String, Object> getMonitoringStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_AUTOSIZE, 1);
        nodeStyleSheet.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        nodeStyleSheet.put(mxConstants.STYLE_FILLCOLOR, "#227910");
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#227910");
        nodeStyleSheet.putAll(getLabelStyleSheet());
        return nodeStyleSheet;
    }

    // Edge stylesheets

    private static Map<String, Object> getEdgeStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_AUTOSIZE, 1);
        nodeStyleSheet.put(mxConstants.STYLE_ROUNDED, true);
        nodeStyleSheet.put(mxConstants.STYLE_SOURCE_PERIMETER_SPACING, 0);
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the connector inactive edge
     */
    public static Map<String, Object> getConnectorInactiveEdgeStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#313131");
        nodeStyleSheet.put(mxConstants.STYLE_STROKEWIDTH, 1);
        nodeStyleSheet.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        nodeStyleSheet.putAll(getEdgeStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the connector active edge
     */
    public static Map<String, Object> getConnectorActiveEdgeStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#6ec020");
        nodeStyleSheet.put(mxConstants.STYLE_STROKEWIDTH, 1);
        nodeStyleSheet.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        nodeStyleSheet.putAll(getEdgeStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the message edge
     */
    public static Map<String, Object> getMessageEdgeStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, "#227910");
        nodeStyleSheet.put(mxConstants.STYLE_DASHED, true);
        nodeStyleSheet.put(mxConstants.STYLE_STROKEWIDTH, 2);
        nodeStyleSheet.put(mxConstants.STYLE_ROUNDED, true);
        nodeStyleSheet.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_LOOP);
        nodeStyleSheet.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
        nodeStyleSheet.putAll(getEdgeStyleSheet());
        return nodeStyleSheet;
    }

    /**
     * @return style sheet defined for the hidden message arrow
     */
    public static Map<String, Object> getMessageHiddenEdgeStyleSheet() {
        final Map<String, Object> nodeStyleSheet = new Hashtable<>();
        nodeStyleSheet.put(mxConstants.STYLE_STROKECOLOR, mxConstants.NONE);
        nodeStyleSheet.putAll(getEdgeStyleSheet());
        return nodeStyleSheet;
    }
}
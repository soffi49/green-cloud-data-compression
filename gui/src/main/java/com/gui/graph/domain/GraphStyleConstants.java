package com.gui.graph.domain;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Class storing all graph style constants
 */
public class GraphStyleConstants {

    /**
     * Node sizes
     */
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int NODE_SIZE_PROPORTION = 40;
    public static final int CLOUD_NETWORK_NODE_SIZE = (int) SCREEN_SIZE.getWidth() / NODE_SIZE_PROPORTION;
    public static final int SERVER_NODE_SIZE =  (int) SCREEN_SIZE.getWidth() / (NODE_SIZE_PROPORTION + 20);
    public static final int GREEN_ENERGY_NODE_SIZE = (int) SCREEN_SIZE.getWidth() / (NODE_SIZE_PROPORTION + 40);
    public static final int MONITORING_NODE_SIZE = (int) SCREEN_SIZE.getWidth() / (NODE_SIZE_PROPORTION + 65);

    /**
     * Node style names
     */
    public static final String CLOUD_NETWORK_INACTIVE_STYLE = "CLOUD_NETWORK_INACTIVE_STYLE";
    public static final String CLOUD_NETWORK_LOW_TRAFFIC_STYLE = "CLOUD_NETWORK_LOW_TRAFFIC_STYLE";
    public static final String CLOUD_NETWORK_MEDIUM_TRAFFIC_STYLE = "CLOUD_NETWORK_MEDIUM_TRAFFIC_STYLE";
    public static final String CLOUD_NETWORK_HIGH_TRAFFIC_STYLE = "CLOUD_NETWORK_HIGH_TRAFFIC_STYLE";

    public static final String SERVER_INACTIVE_STYLE = "SERVER_INACTIVE_STYLE";
    public static final String SERVER_ACTIVE_STYLE = "SERVER_ACTIVE_STYLE";
    public static final String SERVER_BACK_UP_POWER_STYLE = "SERVER_BACK_UP_POWER_STYLE";
    public static final String SERVER_ON_HOLD_STYLE = "SERVER_ON_HOLD_STYLE";

    public static final String GREEN_SOURCE_INACTIVE_STYLE = "GREEN_SOURCE_INACTIVE_STYLE";
    public static final String GREEN_SOURCE_ACTIVE_STYLE = "GREEN_SOURCE_ACTIVE_STYLE";
    public static final String GREEN_SOURCE_ON_HOLD_STYLE = "GREEN_SOURCE_ON_HOLD_STYLE";

    public static final String MONITORING_STYLE = "MONITORING_STYLE";

    /**
     * Edge style names
     */
    public static final String CONNECTOR_EDGE_STYLE = "CONNECTOR_EDGE_STYLE";
    public static final String CONNECTOR_EDGE_ACTIVE_STYLE = "CONNECTOR_EDGE_ACTIVE_STYLE";
    public static final String MESSAGE_EDGE_STYLE = "MESSAGE_EDGE_STYLE";
    public static final String MESSAGE_HIDDEN_EDGE_STYLE = "MESSAGE_HIDDEN_EDGE_STYLE";
}

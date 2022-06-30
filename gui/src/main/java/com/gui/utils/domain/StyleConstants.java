package com.gui.utils.domain;

import java.awt.*;

/**
 * Class stores constants used in styling swing objects
 */
public class StyleConstants {

    /**
     * Color palette constants
     */
    public static final Color DARK_BLUE_COLOR = new Color(12, 44, 82);
    public static final Color BLUE_COLOR = new Color(31, 66, 103);
    public static final Color LIGHT_BLUE_COLOR = new Color(94, 157, 200);
    public static final Color LIGHT_GRAY_COLOR = new Color(158, 171, 176);


    /**
     * Font constants
     */
    public static final String FONT_FAMILY = "Dialog";
    public static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 17);
    public static final Font FIRST_HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font SECOND_HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font DESCRIPTION_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);


    /**
     * CSS class style constants
     */
    public static final String EDGE_MESSAGE_STYLE = "messageFlow";
    public static final String EDGE_HIDDEN_MESSAGE_STYLE = "messageFlowHidden";
    public static final String LABEL_STYLE = "labelStyle";
    public static final String CLIENT_STYLE = "client";
    public static final String CLOUD_NETWORK_STYLE = "cloudNetwork";
    public static final String CLOUD_NETWORK_INACTIVE_STYLE = "cloudNetworkInactive";
    public static final String CLOUD_NETWORK_LOW_STYLE = "cloudNetworkLow";
    public static final String CLOUD_NETWORK_MEDIUM_STYLE = "cloudNetworkMedium";
    public static final String CLOUD_NETWORK_HIGH_STYLE = "cloudNetworkHigh";
    public static final String GREEN_ENERGY_STYLE = "greenEnergy";
    public static final String GREEN_ENERGY_INACTIVE_STYLE = "greenEnergyInactive";
    public static final String GREEN_ENERGY_ACTIVE_STYLE = "greenEnergyActive";
    public static final String MONITORING_STYLE = "monitoring";
    public static final String SERVER_STYLE = "server";
    public static final String SERVER_INACTIVE_STYLE = "serverInactive";
    public static final String SERVER_ACTIVE_STYLE = "serverActive";
}

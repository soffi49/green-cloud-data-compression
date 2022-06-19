package com.gui.domain;

import java.util.List;

public class StyleUtils {
    public static final String LABEL_STYLE = "labelStyle";
    public static final String CLIENT_STYLE = "client";
    public static final String CLOUD_NETWORK_STYLE = "cloudNetwork";
    public static final String GREEN_ENERGY_STYLE = "greenEnergy";
    public static final String MONITORING_STYLE = "monitoring";
    public static final String SERVER_STYLE = "server";

    public static String concatenateStyles(final List<String> style) {
        return String.join(", ", style);
    }

}

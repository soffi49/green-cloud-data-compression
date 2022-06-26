package com.gui.utils;

import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

/**
 * Class provides set of utilities connected with objects' styling
 */
public class StyleUtils {

    /**
     * Method concatenates multiple CSS classes
     *
     * @param style list of css classes
     * @return concatenated classes
     */
    public static String concatenateStyles(final List<String> style) {
        return String.join(", ", style);
    }

    /**
     * Method formats the input string into a string enclosed inside html tags
     *
     * @param string string to be formatted
     * @return string enclosed by html tags
     */
    public static String formatToHTML(final String string) {
        return String.format("<html><p> %s </p></html>", string);
    }

    /**
     * Method uses simplified formula to transform font height from points to pixels
     *
     * @param font font for which the height is to be computed
     * @return height of the text in pixels
     */
    public static double getFontPixels(final Font font) {
        return  font.getSize() * 1.33;
    }

    /**
     * Method creates the horizontal separator
     *
     * @param color color of the separator
     * @return separator being JSeparator object
     */
    public static JSeparator createSeparator(final Color color){
        final JSeparator separator = new JSeparator();
        separator.setBackground(color);
        separator.setForeground(color);
        return separator;
    }

    /**
     * Method creates the shadow to be added to some JPanel
     *
     * @return border being a shadow
     */
    public static Border createCardShadow() {
        final DropShadowBorder shadow = new DropShadowBorder();
        shadow.setShadowColor(Color.GRAY);
        shadow.setShowLeftShadow(true);
        shadow.setShowRightShadow(true);
        shadow.setShowBottomShadow(true);
        shadow.setShowTopShadow(true);
        return shadow;
    }
}

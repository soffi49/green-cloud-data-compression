package com.gui.utils;

import static com.gui.utils.domain.StyleConstants.*;
import static com.gui.utils.domain.StyleConstants.LIGHT_GRAY_COLOR;

import com.gui.domain.types.LabelEnum;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class provides set of utilities connected with GUI objects' styling or creation
 */
public class GUIUtils {

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
     * Method creates the horizontal separator
     *
     * @param color color of the separator
     * @return separator being JSeparator object
     */
    public static JSeparator createSeparator(final Color color) {
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

    /**
     * Method creates JLabel with given font style and foreground color
     *
     * @param font       font style for JLabel
     * @param foreground font color for JLabel
     * @param text       JLabel text
     * @return styled JLabel
     */
    public static JLabel createJLabel(final Font font, final Color foreground, final String text) {
        final JLabel jLabel = new JLabel(text);
        jLabel.setFont(font);
        jLabel.setForeground(foreground);
        return jLabel;
    }

    /**
     * Method creates styled sub-panel
     *
     * @param panelName name of the panel
     * @param layout    layout of the panel
     * @return styled JPanel
     */
    public static JPanel createDefaultSubPanel(final String panelName, final LayoutManager layout) {
        final JPanel panel = new JPanel();
        panel.setName(panelName);
        panel.setBackground(Color.WHITE);
        panel.setLayout(layout);
        panel.setBorder(createCardShadow());
        return panel;
    }

    /**
     * Method creates the panel displaying a list of labels together with their descriptions
     *
     * @param labelMap map containing set of values as JLabels together with their String descriptions
     * @return JPanel being the list panel
     */
    public static JPanel createLabelListPanel(final Map<LabelEnum, JLabel> labelMap) {
        final MigLayout layout = new MigLayout(new LC().fillX().gridGapX("15px"));
        final JPanel panel = new JPanel();
        final String rowHeight = String.format("%d%%",100/labelMap.size());
        panel.setBackground(Color.WHITE);
        panel.setLayout(layout);
        panel.add(createSeparator(LIGHT_GRAY_COLOR), new CC().spanX().growX().wrap());
        labelMap.forEach((labelType, labelValue) -> {
            final JLabel headerLabel = createJLabel(DESCRIPTION_FONT, BLUE_COLOR, labelType.getLabel());
            panel.add(headerLabel, new CC().grow().height(rowHeight).span(2));
            panel.add(labelValue, new CC().grow().height(rowHeight).wrap());
            panel.add(createSeparator(LIGHT_GRAY_COLOR), new CC().spanX().growX().wrap().gapY("2px", "5px"));
        });
        return panel;
    }
}

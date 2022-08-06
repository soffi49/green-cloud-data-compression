package com.gui.event;

import static com.gui.event.domain.EventGUIConstants.BUTTON_PROPORTION;
import static com.gui.event.domain.EventGUIConstants.INPUT_FIELD_PROPORTION;
import static com.gui.event.domain.EventGUIConstants.MAX_POWER_INPUT_LABEL;
import static com.gui.event.domain.EventGUIConstants.POWER_SHORTAGE_TITLE_LABEL;
import static com.gui.event.domain.EventTypeEnum.POWER_SHORTAGE;
import static com.gui.gui.utils.GUIComponentUtils.createButton;
import static com.gui.gui.utils.GUIComponentUtils.createNumericTextField;
import static com.gui.gui.utils.GUIComponentUtils.createSeparator;
import static com.gui.gui.utils.GUILabelUtils.createJLabel;
import static com.gui.gui.utils.GUILayoutUtils.addComponentToGridWithHorizontalProportion;
import static com.gui.gui.utils.GUILayoutUtils.addHeaderComponentsToGrid;
import static com.gui.gui.utils.GUIPanelUtils.createShadowPanel;
import static com.gui.gui.utils.GUIPanelUtils.createVerticallyScrolledPanel;
import static com.gui.gui.utils.domain.GUIStyleConstants.GRAY_4_COLOR;
import static com.gui.gui.utils.domain.GUIStyleConstants.SECOND_HEADER_FONT;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.gui.agents.domain.AgentNode;
import com.gui.event.domain.EventTypeEnum;

public class EventGUIServiceImpl implements EventGUIService {

	private final EventService eventService;

	public EventGUIServiceImpl() {
		this.eventService = new EventServiceImpl();
	}

	@Override
	public Component createEventPanelForAgent(AgentNode agentNode) {
		final JPanel eventPanel = new JPanel();
		final BoxLayout eventPanelLayout = new BoxLayout(eventPanel, BoxLayout.PAGE_AXIS);
		eventPanel.setLayout(eventPanelLayout);
		agentNode.getAgentEvents()
				.keySet()
				.forEach(eventType -> mapEventTypeToPanelRow(agentNode, eventType).ifPresent(eventPanel::add));

		final JScrollPane eventScrollPane = createVerticallyScrolledPanel(eventPanel);
		eventScrollPane.setName(agentNode.getAgentName());
		return eventScrollPane;
	}

	private Optional<JPanel> mapEventTypeToPanelRow(final AgentNode agentNode, final EventTypeEnum eventType) {
		switch (eventType) {
			case POWER_SHORTAGE:
				return Optional.of(createPowerShortageEventRow(agentNode));
			default:
				return Optional.empty();
		}
	}

	private JPanel createPowerShortageEventRow(final AgentNode agentNode) {
		final JFormattedTextField powerShortageInput = createNumericTextField(MAX_POWER_INPUT_LABEL);
		final JButton powerShortageButton = createButton(POWER_SHORTAGE.getEventLabelStart(),
				e -> eventService.causePowerShortage(powerShortageInput, agentNode, (JButton) e.getSource()));
		final JPanel powerShortagePanel = createShadowPanel(new GridBagLayout());
		addHeaderComponentsToGrid(List.of(createJLabel(SECOND_HEADER_FONT, GRAY_4_COLOR, POWER_SHORTAGE_TITLE_LABEL),
				createSeparator(GRAY_4_COLOR)), powerShortagePanel);
		addComponentToGridWithHorizontalProportion(powerShortageInput, powerShortagePanel, INPUT_FIELD_PROPORTION,
				false);
		addComponentToGridWithHorizontalProportion(powerShortageButton, powerShortagePanel, BUTTON_PROPORTION, true);
		return powerShortagePanel;
	}
}

package com.gui.event;

import static com.gui.event.domain.EventGUIConstants.DISABLED_BUTTON;
import static com.gui.event.domain.EventTypeEnum.POWER_SHORTAGE;
import static com.gui.gui.utils.GUIComponentUtils.addStyleToButton;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.Timer;

import com.gui.agents.AbstractAgentNode;
import com.gui.event.domain.PowerShortageEvent;

public class EventServiceImpl implements EventService {

	@Override
	public void causePowerShortage(final JFormattedTextField maximumPowerInput, final AbstractAgentNode agentNode,
			final JButton eventButton) {
		if (!maximumPowerInput.equals("")) {
			if (Objects.nonNull(agentNode) && agentNode.getAgentEvents().containsKey(POWER_SHORTAGE)) {
				final int maximumPower = Integer.parseInt(maximumPowerInput.getText());
				final boolean isPowerShortageActive = agentNode.getAgentEvents().get(POWER_SHORTAGE);
				final String nextStateButtonLabel = isPowerShortageActive ?
						POWER_SHORTAGE.getEventLabelStart() :
						POWER_SHORTAGE.getEventLabelFinish();

				if (isPowerShortageActive) {
					agentNode.getAgentEvents().replace(POWER_SHORTAGE, false);
					agentNode.addEventToStack(new PowerShortageEvent(getPowerShortageTime(), maximumPower, true));
				} else {
					agentNode.getAgentEvents().replace(POWER_SHORTAGE, true);
					agentNode.addEventToStack(new PowerShortageEvent(getPowerShortageTime(), maximumPower, false));
				}
				disableEventForTimeout(eventButton, nextStateButtonLabel, List.of(maximumPowerInput));
			}
		}
	}

	private OffsetDateTime getPowerShortageTime() {
		return OffsetDateTime.now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime().plusSeconds(1);
	}

	private void disableEventForTimeout(final JButton button, final String labelOnButtonEnable,
			final List<Component> additionalComponents) {
		button.setEnabled(false);
		additionalComponents.forEach(component -> component.setEnabled(false));
		addStyleToButton(button, DISABLED_BUTTON, true);
		final ActionListener enableButtonAction = (e) -> {
			addStyleToButton(button, labelOnButtonEnable, false);
			button.setEnabled(true);
			additionalComponents.forEach(component -> component.setEnabled(true));
		};
		final Timer enableButtonTimer = new Timer(2000, enableButtonAction);
		enableButtonTimer.setRepeats(false);
		enableButtonTimer.start();
	}
}

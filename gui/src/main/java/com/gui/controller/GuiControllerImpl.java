package com.gui.controller;

import static com.greencloud.commons.time.TimeConstants.SECONDS_PER_HOUR;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

import com.gui.agents.AbstractAgentNode;
import com.gui.event.domain.PowerShortageEvent;
import com.gui.message.ImmutableRemoveAgentMessage;
import com.gui.message.ImmutableReportSystemStartTimeMessage;
import com.gui.message.ImmutableUpdateSingleValueMessage;
import com.gui.websocket.GuiWebSocketClient;
import com.gui.websocket.GuiWebSocketListener;

public class GuiControllerImpl implements GuiController {

	private static GuiWebSocketClient webSocketClient;
	private static GuiWebSocketListener webSocketListener;

	public GuiControllerImpl() {
	}

	public GuiControllerImpl(final String mainHostUri) {
		if (webSocketClient == null) {
			webSocketClient = new GuiWebSocketClient(URI.create(mainHostUri));
		}
		if (webSocketListener == null) {
			webSocketListener = new GuiWebSocketListener(URI.create(mainHostUri + "powerShortage"));
		}
	}

	@Override
	public void run() {
		webSocketClient.connect();
		webSocketListener.connect();
	}

	@Override
	public void reportSystemStartTime(final Instant time) {
		webSocketClient.send(ImmutableReportSystemStartTimeMessage.builder()
				.time(time.toEpochMilli())
				.secondsPerHour(SECONDS_PER_HOUR)
				.build());
	}

	@Override
	public void addAgentNodeToGraph(final AbstractAgentNode agent) {
		if (Objects.nonNull(agent)) {
			webSocketListener.addAgentNode(agent);
			agent.addToGraph(webSocketClient);
		}
	}

	@Override
	public void removeAgentNodeFromGraph(final AbstractAgentNode agentNode) {
		webSocketClient.send(ImmutableRemoveAgentMessage.builder()
				.agentName(agentNode.getAgentName())
				.build());
	}

	@Override
	public void updateClientsCountByValue(final int value) {
		webSocketClient.send(ImmutableUpdateSingleValueMessage.builder()
				.data(value)
				.type("UPDATE_CURRENT_CLIENTS")
				.build());
	}

	@Override
	public void updateActiveJobsCountByValue(final int value) {
		webSocketClient.send(ImmutableUpdateSingleValueMessage.builder()
				.data(value)
				.type("UPDATE_CURRENT_ACTIVE_JOBS")
				.build());
	}

	@Override
	public void updateAllJobsCountByValue(final int value) {
		webSocketClient.send(ImmutableUpdateSingleValueMessage.builder()
				.data(value)
				.type("UPDATE_CURRENT_PLANNED_JOBS")
				.build());
	}

	@Override
	public void updateFailedJobsCountByValue(final int value) {
		webSocketClient.send(ImmutableUpdateSingleValueMessage.builder()
				.data(value)
				.type("INCREMENT_FAILED_JOBS")
				.build());
	}

	@Override
	public void updateFinishedJobsCountByValue(final int value) {
		webSocketClient.send(ImmutableUpdateSingleValueMessage.builder()
				.data(value)
				.type("INCREMENT_FINISHED_JOBS")
				.build());
	}

	@Override
	public void triggerPowerShortageEvent(final PowerShortageEvent powerShortageEvent, final String agentName) {
		webSocketListener.triggerPowerShortage(powerShortageEvent, agentName);
	}
}

package com.gui.agents;

import static java.lang.Double.parseDouble;
import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.Optional;

import com.greencloud.commons.args.agent.greenenergy.GreenEnergyAgentArgs;
import com.greencloud.commons.args.agent.greenenergy.ImmutableGreenEnergyNodeArgs;
import com.greencloud.commons.domain.location.ImmutableLocation;
import com.greencloud.commons.domain.location.Location;
import com.gui.event.domain.PowerShortageEvent;
import com.gui.message.ImmutableRegisterAgentMessage;
import com.gui.message.ImmutableSetNumericValueMessage;
import com.gui.message.ImmutableUpdateServerConnectionMessage;
import com.gui.message.domain.ImmutableServerConnection;
import com.gui.websocket.GuiWebSocketClient;

/**
 * Agent node class representing the green energy source
 */
public class GreenEnergyAgentNode extends AbstractNetworkAgentNode implements Serializable {

	private Location location;
	private String monitoringAgent;
	private String serverAgent;
	private String energyType;
	private double weatherPredictionError;

	public GreenEnergyAgentNode() {
		super();
	}

	/**
	 * Green energy source node constructor
	 *
	 * @param args arguments provided for green energy agent creation
	 */
	public GreenEnergyAgentNode(GreenEnergyAgentArgs args) {
		super(args.getName(), parseDouble(args.getMaximumCapacity()));
		this.location = new ImmutableLocation(parseDouble(args.getLatitude()), parseDouble(args.getLongitude()));
		this.serverAgent = args.getOwnerSever();
		this.monitoringAgent = args.getMonitoringAgent();
		this.energyType = args.getEnergyType();
		this.weatherPredictionError = Double.parseDouble(args.getWeatherPredictionError());
	}

	@Override
	public void addToGraph(GuiWebSocketClient webSocketClient) {
		this.webSocketClient = webSocketClient;
		webSocketClient.send(ImmutableRegisterAgentMessage.builder()
				.agentType("GREEN_ENERGY")
				.data(ImmutableGreenEnergyNodeArgs.builder()
						.maximumCapacity(String.valueOf(initialMaximumCapacity))
						.name(agentName)
						.agentLocation(location)
						.monitoringAgent(monitoringAgent)
						.serverAgent(serverAgent)
						.energyType(energyType)
						.weatherPredictionError(weatherPredictionError * 100)
						.build())
				.build());
	}

	/**
	 * Function updates current value of weather prediction error
	 *
	 * @param value new weather prediction error value
	 */
	public void updatePredictionError(final double value) {
		webSocketClient.send(ImmutableSetNumericValueMessage.builder()
				.data(value * 100)
				.agentName(agentName)
				.type("SET_WEATHER_PREDICTION_ERROR")
				.build());
	}

	/**
	 * Function updates the amount of available green energy for given agent
	 *
	 * @param value amount of available green energy
	 */
	public void updateGreenEnergyAmount(final double value) {
		webSocketClient.send(ImmutableSetNumericValueMessage.builder()
				.data(value)
				.agentName(agentName)
				.type("SET_AVAILABLE_GREEN_ENERGY")
				.build());
	}

	/**
	 * Function updates in the GUI the connection state for given server
	 *
	 * @param serverName  name of the server connected/disconnected to Green Source
	 * @param isConnected flag indicating if the server should be connected/disconnected
	 */
	public void updateServerConnection(final String serverName, final boolean isConnected) {
		webSocketClient.send(ImmutableUpdateServerConnectionMessage.builder()
				.agentName(this.agentName)
				.data(ImmutableServerConnection.builder()
						.isConnected(isConnected)
						.serverName(serverName)
						.build())
				.build());

	}

	public Optional<PowerShortageEvent> getEvent() {
		return ofNullable((PowerShortageEvent) eventsQueue.poll());
	}
}

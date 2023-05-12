package com.gui.websocket;

import static java.util.Optional.ofNullable;

import java.net.URI;

/**
 * Class stores the information about available websocket connections
 *
 * <p>AGENTS_WEB_SOCKET - websocket via which the application sends the data related to cloud network component agents</p>
 * <p>CLIENTS_WEB_SOCKET - websocket via which the application sends the data related to client agents</p>
 * <p>CLIENTS_WEB_SOCKET - websocket via which the application sends the data related to managing system agent</p>
 * <p>CLOUD_NETWORK_SOCKET - websocket via which the application sends the overall statistics of the cloud network</p>
 * <p>EVENT_SOCKET - websocket via which the application listens for the external events triggered on the system</p>
 */
public class WebSocketConnections {

	private static GuiWebSocketClient AGENTS_WEB_SOCKET;
	private static GuiWebSocketClient CLIENTS_WEB_SOCKET;
	private static GuiWebSocketClient MANAGING_SYSTEM_SOCKET;
	private static GuiWebSocketClient CLOUD_NETWORK_SOCKET;
	private static GuiWebSocketListener EVENT_SOCKET;

	/**
	 * Method initializes the connection of websockets for the same url
	 *
	 * @param singleUrl websocket url
	 */
	public static void initialize(final String singleUrl) {
		AGENTS_WEB_SOCKET = initializeSocket(AGENTS_WEB_SOCKET, singleUrl);
		CLIENTS_WEB_SOCKET = initializeSocket(CLIENTS_WEB_SOCKET, singleUrl);
		MANAGING_SYSTEM_SOCKET = initializeSocket(MANAGING_SYSTEM_SOCKET, singleUrl);
		CLOUD_NETWORK_SOCKET = initializeSocket(CLOUD_NETWORK_SOCKET, singleUrl);
		EVENT_SOCKET = initializeListener(EVENT_SOCKET, singleUrl);
	}

	/**
	 * Method initializes the connection to websockets
	 *
	 * @param agentsUrl   agents websocket url
	 * @param clientsUrl  clients websocket url
	 * @param managingUrl managing agent websocket url
	 * @param networkUrl  network statistics websocket url
	 * @param eventUrl    event handler websocket url
	 */
	public static void initialize(final String agentsUrl, final String clientsUrl,
			final String managingUrl, final String networkUrl, final String eventUrl) {
		AGENTS_WEB_SOCKET = initializeSocket(AGENTS_WEB_SOCKET, agentsUrl);
		CLIENTS_WEB_SOCKET = initializeSocket(CLIENTS_WEB_SOCKET, clientsUrl);
		MANAGING_SYSTEM_SOCKET = initializeSocket(MANAGING_SYSTEM_SOCKET, managingUrl);
		CLOUD_NETWORK_SOCKET = initializeSocket(CLOUD_NETWORK_SOCKET, networkUrl);
		EVENT_SOCKET = initializeListener(EVENT_SOCKET, eventUrl);
	}

	/**
	 * Method connects to all websockets
	 */
	public static void connect() {
		AGENTS_WEB_SOCKET.connect();
		CLIENTS_WEB_SOCKET.connect();
		MANAGING_SYSTEM_SOCKET.connect();
		CLOUD_NETWORK_SOCKET.connect();
		EVENT_SOCKET.connect();
	}

	public static GuiWebSocketClient getAgentsWebSocket() {
		return AGENTS_WEB_SOCKET;
	}

	public static GuiWebSocketClient getClientsWebSocket() {
		return CLIENTS_WEB_SOCKET;
	}

	public static GuiWebSocketClient getManagingSystemSocket() {
		return MANAGING_SYSTEM_SOCKET;
	}

	public static GuiWebSocketClient getCloudNetworkSocket() {
		return CLOUD_NETWORK_SOCKET;
	}

	public static GuiWebSocketListener getEventSocket() {
		return EVENT_SOCKET;
	}

	private static GuiWebSocketClient initializeSocket(final GuiWebSocketClient socket, final String url) {
		return ofNullable(socket).orElse(new GuiWebSocketClient(URI.create(url)));
	}

	private static GuiWebSocketListener initializeListener(final GuiWebSocketListener listener, final String url) {
		return ofNullable(listener).orElse(new GuiWebSocketListener(URI.create(url + "powerShortage")));
	}
}

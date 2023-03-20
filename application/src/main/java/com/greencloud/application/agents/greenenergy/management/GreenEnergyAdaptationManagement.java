package com.greencloud.application.agents.greenenergy.management;

import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.ADAPTATION_CONNECT_SERVER_LOG;
import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.ADAPTATION_DISCONNECT_SERVER_LOG;
import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.ADAPTATION_INCREASE_ERROR_LOG;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.greencloud.application.agents.AbstractAgentManagement;
import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.behaviour.adaptation.InitiateGreenSourceDeactivation;
import com.greencloud.application.agents.greenenergy.behaviour.adaptation.InitiateNewServerConnection;
import com.greencloud.application.agents.greenenergy.domain.GreenSourceDisconnection;
import com.greencloud.commons.managingsystem.planner.AdjustGreenSourceErrorParameters;
import com.greencloud.commons.managingsystem.planner.ChangeGreenSourceConnectionParameters;

import jade.lang.acl.ACLMessage;

/**
 * Set of methods used to adapt the current configuration of Green Energy Agent
 */
public class GreenEnergyAdaptationManagement extends AbstractAgentManagement {

	private static final Logger logger = getLogger(GreenEnergyAdaptationManagement.class);

	private final GreenEnergyAgent greenEnergyAgent;
	private GreenSourceDisconnection greenSourceDisconnection;

	/**
	 * Default constructor
	 *
	 * @param greenEnergyAgent - agent representing given source
	 */
	public GreenEnergyAdaptationManagement(GreenEnergyAgent greenEnergyAgent) {
		this.greenEnergyAgent = greenEnergyAgent;
		this.greenSourceDisconnection = new GreenSourceDisconnection();
	}

	/**
	 * Method adapts the current weather prediction error of Green Energy Agent
	 *
	 * @param params adaptation parameters
	 */
	public boolean adaptAgentWeatherPredictionError(final AdjustGreenSourceErrorParameters params) {
		final double currentError = greenEnergyAgent.getWeatherPredictionError();
		final double newError = currentError + params.getPercentageChange();
		final String log = params.getPercentageChange() > 0 ? "Increasing" : "Decreasing";

		logger.info(ADAPTATION_INCREASE_ERROR_LOG, log, currentError, newError);

		greenEnergyAgent.setWeatherPredictionError(newError);
		greenEnergyAgent.manage().updateGUI();
		return true;
	}

	/**
	 * Method communicates with indicated Server in order o establish with it a connection
	 *
	 * @param params            adaptation parameters
	 * @param adaptationMessage original adaptation message
	 */
	public void connectNewServerToGreenSource(final ChangeGreenSourceConnectionParameters params,
			final ACLMessage adaptationMessage) {
		final String serverName = params.getServerName().split("@")[0];
		logger.info(ADAPTATION_CONNECT_SERVER_LOG, serverName);

		greenEnergyAgent.addBehaviour(InitiateNewServerConnection.create(greenEnergyAgent, adaptationMessage,
				params.getServerName()));
	}

	/**
	 * Method disconnects a green source from selected server
	 *
	 * @param params            adaptation parameters
	 * @param adaptationMessage original adaptation message
	 */
	public void disconnectGreenSourceFromServer(final ChangeGreenSourceConnectionParameters params,
			final ACLMessage adaptationMessage) {
		final String serverName = params.getServerName().split("@")[0];
		logger.info(ADAPTATION_DISCONNECT_SERVER_LOG, serverName);

		getDisconnectionState().setBeingDisconnected(true);
		getDisconnectionState().setOriginalAdaptationMessage(adaptationMessage);
		greenEnergyAgent.addBehaviour(InitiateGreenSourceDeactivation.create(greenEnergyAgent, params.getServerName()));
	}

	public GreenSourceDisconnection getDisconnectionState() {
		return greenSourceDisconnection;
	}

	public void setDisconnectionState(GreenSourceDisconnection greenSourceDisconnection) {
		this.greenSourceDisconnection = greenSourceDisconnection;
	}

}

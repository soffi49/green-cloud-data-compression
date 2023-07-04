package com.greencloud.application.agents.greenenergy;

import static com.database.knowledge.domain.action.AdaptationActionEnum.CONNECT_GREEN_SOURCE;
import static com.database.knowledge.domain.action.AdaptationActionEnum.DECREASE_GREEN_SOURCE_ERROR;
import static com.database.knowledge.domain.action.AdaptationActionEnum.DISCONNECT_GREEN_SOURCE;
import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_GREEN_SOURCE_ERROR;
import static com.greencloud.application.domain.agent.enums.AgentManagementEnum.ADAPTATION_MANAGEMENT;
import static jade.lang.acl.ACLMessage.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.greencloud.application.agents.greenenergy.management.GreenEnergyAdaptationManagement;
import com.greencloud.application.agents.greenenergy.management.GreenEnergyStateManagement;
import com.greencloud.commons.managingsystem.planner.ImmutableAdjustGreenSourceErrorParameters;
import com.greencloud.commons.managingsystem.planner.ImmutableChangeGreenSourceConnectionParameters;

import jade.lang.acl.ACLMessage;

class AbstractGreenEnergyAgentUnitTest {

	private static final double INITIAL_WEATHER_PREDICTION_ERROR = 0.02;

	@Mock
	private GreenEnergyAgent agent;
	@Mock
	private GreenEnergyAdaptationManagement mockAdaptationManagement;

	@BeforeEach
	void init() {
		agent = spy(GreenEnergyAgent.class);
		mockAdaptationManagement = spy(new GreenEnergyAdaptationManagement(agent));
		agent.addAgentManagement(mockAdaptationManagement, ADAPTATION_MANAGEMENT);
		var manager = spy(new GreenEnergyStateManagement(agent));

		doReturn(manager).when(agent).manage();
	}

	@Test
	@DisplayName("Test executing adaptation action for incrementing error")
	void testExecuteActionIncrementError() {
		var adaptationParams = ImmutableAdjustGreenSourceErrorParameters.builder()
				.percentageChange(0.04)
				.build();
		agent.setWeatherPredictionError(INITIAL_WEATHER_PREDICTION_ERROR);
		agent.executeAction(INCREASE_GREEN_SOURCE_ERROR, adaptationParams);

		assertThat(agent.getWeatherPredictionError()).isEqualTo(0.06);
	}

	@Test
	@DisplayName("Test executing adaptation action for decrementing error")
	void testExecuteActionDecrementError() {
		var adaptationParams = ImmutableAdjustGreenSourceErrorParameters.builder()
				.percentageChange(-0.04)
				.build();
		agent.setWeatherPredictionError(0.06);
		agent.executeAction(DECREASE_GREEN_SOURCE_ERROR, adaptationParams);

		assertThat(agent.getWeatherPredictionError()).isCloseTo(0.02, Offset.offset(0.0001));
	}

	@Test
	@DisplayName("Test executing adaptation action for connecting green source to server")
	void testExecuteActionForConnectingGreenSource() {
		var adaptationParams = ImmutableChangeGreenSourceConnectionParameters.builder()
				.serverName("test_server")
				.build();
		var message = new ACLMessage(REQUEST);
		agent.executeAction(CONNECT_GREEN_SOURCE, adaptationParams, message);

		verify(mockAdaptationManagement).connectNewServerToGreenSource(adaptationParams, message);
	}

	@Test
	@DisplayName("Test executing adaptation action for disconnecting green source from server")
	void testExecuteActionForDisconnectGreenSource() {
		var adaptationParams = ImmutableChangeGreenSourceConnectionParameters.builder()
				.serverName("test_server")
				.build();
		var message = new ACLMessage(REQUEST);
		agent.executeAction(DISCONNECT_GREEN_SOURCE, adaptationParams, message);

		verify(mockAdaptationManagement).disconnectGreenSourceFromServer(adaptationParams, message);

		assertThat(mockAdaptationManagement.getDisconnectionState()).satisfies(state -> {
			assertThat(state.getOriginalAdaptationMessage()).isEqualTo(message);
			assertThat(state.isBeingDisconnected()).isTrue();
		});
	}
}

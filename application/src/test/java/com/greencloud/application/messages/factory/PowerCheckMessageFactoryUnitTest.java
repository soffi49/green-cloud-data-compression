package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.constants.MessageProtocolConstants.ON_HOLD_JOB_CHECK_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.factory.PowerCheckMessageFactory.preparePowerCheckRequest;
import static com.greencloud.application.messages.factory.PowerCheckMessageFactory.prepareWeatherDataResponse;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_MONITORING;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_SERVER;
import static com.greencloud.application.messages.fixtures.Fixtures.buildLocation;
import static com.greencloud.application.messages.fixtures.Fixtures.buildLocationContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildMonitoringData;
import static com.greencloud.application.messages.fixtures.Fixtures.buildMonitoringDataContent;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class PowerCheckMessageFactoryUnitTest {

	@Test
	@DisplayName("Test prepare power check request message")
	void testPreparePowerCheckRequest() {
		// given
		var conversationId = PERIODIC_WEATHER_CHECK_PROTOCOL;
		var protocol = PERIODIC_WEATHER_CHECK_PROTOCOL;

		var expectedContent = buildLocationContent();

		var mockGreenEnergy = mock(GreenEnergyAgent.class);
		doReturn(TEST_MONITORING).when(mockGreenEnergy).getMonitoringAgent();
		doReturn(buildLocation()).when(mockGreenEnergy).getLocation();
		doReturn(0.04).when(mockGreenEnergy).getWeatherPredictionError();

		// when
		final ACLMessage result = preparePowerCheckRequest(mockGreenEnergy, null, conversationId, protocol);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(result.getConversationId()).isEqualTo(conversationId);
		assertThat(result.getPerformative()).isEqualTo(REQUEST);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_MONITORING));
	}

	@Test
	@DisplayName("Test weather data response message")
	void testPrepareWeatherDataResponse() {
		// given
		var monitoringData = buildMonitoringData();

		var request = new ACLMessage(REQUEST);
		request.setSender(TEST_MONITORING);
		request.setProtocol(ON_HOLD_JOB_CHECK_PROTOCOL);
		request.setReplyWith("R1671062222360_1");
		request.setConversationId("C805691330_Monitor_1671062222359_0");

		var expectedContent = buildMonitoringDataContent();

		// when
		final ACLMessage result = prepareWeatherDataResponse(monitoringData, request);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(ON_HOLD_JOB_CHECK_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo("C805691330_Monitor_1671062222359_0");
		assertThat(result.getInReplyTo()).isEqualTo("R1671062222360_1");
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_MONITORING));
	}
}

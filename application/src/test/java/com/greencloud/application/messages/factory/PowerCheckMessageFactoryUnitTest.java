package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.constants.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;
import static com.greencloud.application.messages.factory.PowerCheckMessageFactory.preparePowerCheckRequest;
import static jade.lang.acl.ACLMessage.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.commons.domain.location.ImmutableLocation;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class PowerCheckMessageFactoryUnitTest {

	@Test
	@DisplayName("Test prepare power check request message")
	void testPreparePowerCheckRequest() {
		final AID mockMonitoring = mock(AID.class);
		doReturn("test_monitoring").when(mockMonitoring).getName();

		final GreenEnergyAgent mockGreenEnergy = mock(GreenEnergyAgent.class);
		doReturn(mockMonitoring).when(mockGreenEnergy).getMonitoringAgent();
		doReturn(ImmutableLocation.builder().latitude(10.0).longitude(20.0).build()).when(
				mockGreenEnergy).getLocation();
		doReturn(0.04).when(mockGreenEnergy).getWeatherPredictionError();

		final String conversationId = PERIODIC_WEATHER_CHECK_PROTOCOL;
		final String protocol = PERIODIC_WEATHER_CHECK_PROTOCOL;

		final String expectedContent =
				"{\"location\":{\"latitude\":10.0,\"longitude\":20.0},"
						+ "\"predictionError\":0.04}";

		final ACLMessage result = preparePowerCheckRequest(mockGreenEnergy, null, conversationId, protocol);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(result.getConversationId()).isEqualTo(conversationId);
		assertThat(result.getPerformative()).isEqualTo(REQUEST);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(mockMonitoring));
	}
}

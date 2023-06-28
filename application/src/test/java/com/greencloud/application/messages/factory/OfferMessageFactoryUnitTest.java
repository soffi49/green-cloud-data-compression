package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.constants.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.factory.OfferMessageFactory.prepareGreenEnergyPowerSupplyOffer;
import static com.greencloud.application.messages.factory.OfferMessageFactory.prepareServerJobOffer;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_CNA;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_SERVER;
import static com.greencloud.application.messages.fixtures.Fixtures.buildClientJob;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.management.ServerStateManagement;
import com.greencloud.application.exception.JobNotFoundException;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class OfferMessageFactoryUnitTest {

	@Test
	@DisplayName("Test making server job offer")
	void testMakeServerJobOffer() {
		// given
		var mockJob = buildClientJob();
		var jobId = "1";
		var servicePrice = 15;
		var mockPower = 100;

		var replyMessage = new ACLMessage(CFP);
		replyMessage.setSender(TEST_CNA);
		replyMessage.setProtocol(CNA_JOB_CFP_PROTOCOL);
		replyMessage.setReplyWith("R1671062222360_1");
		replyMessage.setConversationId("C805691330_CNA_1671062222359_0");

		var expectedContent = "{\"servicePrice\":15.0,\"availablePower\":100,\"jobId\":\"1\"}";

		final ServerAgent mockServer = mock(ServerAgent.class);
		final ServerStateManagement mockManagement = mock(ServerStateManagement.class);

		doReturn(new ConcurrentHashMap<>(Map.of(mockJob, PROCESSING))).when(mockServer).getServerJobs();
		doReturn(mockManagement).when(mockServer).manage();
		doReturn(mockPower).when(mockManagement).getAvailableCapacity(mockJob, null, null);

		// when
		final ACLMessage result = prepareServerJobOffer(mockServer, servicePrice, jobId, replyMessage);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CNA_JOB_CFP_PROTOCOL);
		assertThat(result.getInReplyTo()).isEqualTo("R1671062222360_1");
		assertThat(result.getConversationId()).isEqualTo("C805691330_CNA_1671062222359_0");
		assertThat(result.getPerformative()).isEqualTo(PROPOSE);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_CNA));
	}

	@Test
	@DisplayName("Test making server job offer for job not found")
	void testMakeServerJobOfferJobNotFound() {
		// given
		var mockJob = buildClientJob();
		var jobId = "2";
		var servicePrice = 15;

		var replyMessage = new ACLMessage(CFP);
		replyMessage.setSender(TEST_CNA);
		replyMessage.setProtocol(CNA_JOB_CFP_PROTOCOL);
		replyMessage.setReplyWith("R1671062222360_1");
		replyMessage.setConversationId("C805691330_CNA_1671062222359_0");

		final ServerAgent mockServer = mock(ServerAgent.class);
		doReturn(new ConcurrentHashMap<>(Map.of(mockJob, PROCESSING))).when(mockServer).getServerJobs();

		// when & then
		assertThatThrownBy(() -> prepareServerJobOffer(mockServer, servicePrice, jobId, replyMessage))
				.isInstanceOf(JobNotFoundException.class)
				.hasMessage("Job does not exists in given agent");
	}

	@Test
	@DisplayName("Test making green energy power supply offer")
	void testMakeGreenEnergyPowerSupplyOffer() {
		// given
		var averageAvailablePower = 150;
		var predictionError = 0.02;
		var jobId = "1";

		var replyMessage = new ACLMessage(CFP);
		replyMessage.setSender(TEST_SERVER);
		replyMessage.setProtocol(SERVER_JOB_CFP_PROTOCOL);
		replyMessage.setReplyWith("R1671062222360_1");
		replyMessage.setConversationId("C805691330_Server_1671062222359_0");

		var expectedContent = "{\"availablePowerInTime\":150.0,"
				+ "\"pricePerPowerUnit\":10.0,"
				+ "\"powerPredictionError\":0.02,"
				+ "\"jobId\":\"1\"}";

		final GreenEnergyAgent mockGreenEnergy = mock(GreenEnergyAgent.class);
		doReturn(10.0).when(mockGreenEnergy).getPricePerPowerUnit();

		// when
		final ACLMessage result = prepareGreenEnergyPowerSupplyOffer(mockGreenEnergy, averageAvailablePower,
				predictionError, jobId, replyMessage);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(SERVER_JOB_CFP_PROTOCOL);
		assertThat(result.getInReplyTo()).isEqualTo("R1671062222360_1");
		assertThat(result.getConversationId()).isEqualTo("C805691330_Server_1671062222359_0");
		assertThat(result.getPerformative()).isEqualTo(PROPOSE);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}
}

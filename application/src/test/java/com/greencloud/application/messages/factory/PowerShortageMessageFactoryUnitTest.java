package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.constants.MessageProtocolConstants.CONFIRMED_TRANSFER_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.FAILED_TRANSFER_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL;
import static com.greencloud.application.messages.factory.PowerShortageMessageFactory.prepareGreenPowerSupplyRequest;
import static com.greencloud.application.messages.factory.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static com.greencloud.application.messages.factory.PowerShortageMessageFactory.prepareJobTransferUpdateMessageForCNA;
import static com.greencloud.application.messages.factory.PowerShortageMessageFactory.preparePowerShortageTransferRequest;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_CNA;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_SERVER;
import static com.greencloud.application.messages.fixtures.Fixtures.buildClientJob;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstance;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstanceContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildPowerShortageTransferContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildPowerShortageTransferJob;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greencloud.application.agents.server.ServerAgent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class PowerShortageMessageFactoryUnitTest {

	@Test
	@DisplayName("Test prepare power shortage transfer request")
	void testPreparePowerShortageTransferRequest() {
		// given
		var jobTransfer = buildPowerShortageTransferJob();
		var expectedContent = buildPowerShortageTransferContent();

		// when
		final ACLMessage result = preparePowerShortageTransferRequest(jobTransfer, TEST_CNA);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(POWER_SHORTAGE_ALERT_PROTOCOL);
		assertThat(result.getPerformative()).isEqualTo(REQUEST);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_CNA));
	}

	@Test
	@DisplayName("Test prepare green power supply request")
	void testPreparePowerGreenPowerSupplyRequest() {
		// given
		var mockJob = buildClientJob();
		var expectedContent = buildJobInstanceContent();

		// when
		final ACLMessage result = prepareGreenPowerSupplyRequest(mockJob, TEST_SERVER);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL);
		assertThat(result.getPerformative()).isEqualTo(REQUEST);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare job transfer update message for CNA")
	void testPrepareJobTransferUpdateMessageForCNA() {
		// given
		var jobInstance = buildJobInstance();
		var protocol = CONFIRMED_TRANSFER_PROTOCOL;
		var expectedContent = buildJobInstanceContent();

		var mockServer = mock(ServerAgent.class);
		doReturn(TEST_CNA).when(mockServer).getOwnerCloudNetworkAgent();

		// when
		final ACLMessage result = prepareJobTransferUpdateMessageForCNA(jobInstance, protocol, mockServer);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_CNA));
	}

	@Test
	@DisplayName("Test prepare job transfer update message for CNA for failure")
	void testPrepareJobTransferUpdateMessageForCNAFailure() {
		// given
		var jobInstance = buildJobInstance();
		var protocol = FAILED_TRANSFER_PROTOCOL;
		var expectedContent = buildJobInstanceContent();

		var mockServer = mock(ServerAgent.class);
		doReturn(TEST_CNA).when(mockServer).getOwnerCloudNetworkAgent();

		// when
		final ACLMessage result = prepareJobTransferUpdateMessageForCNA(jobInstance, protocol, mockServer);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(result.getPerformative()).isEqualTo(FAILURE);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_CNA));
	}

	@Test
	@DisplayName("Test prepare job power shortage information")
	void testPrepareJobPowerShortageInformation() {
		// given
		var jobInstance = buildJobInstance();
		var expectedContent = buildJobInstanceContent();
		final String protocol = POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;

		// when
		final ACLMessage result = prepareJobPowerShortageInformation(jobInstance, protocol, TEST_SERVER);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}
}

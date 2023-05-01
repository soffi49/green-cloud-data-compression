package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.constants.MessageConversationConstants.DELAYED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.FAILED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.FINISH_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.POSTPONED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.RE_SCHEDULED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.SPLIT_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.STARTED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.ANNOUNCED_JOB_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.CANCEL_JOB_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.CHANGE_JOB_STATUS_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.FAILED_JOB_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.MANUAL_JOB_FINISH_PROTOCOL;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobAdjustmentMessage;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobAnnouncementMessage;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobCancellationMessage;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobFinishMessage;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobStartedMessage;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobStatusMessageForCNA;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobStatusMessageForScheduler;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareManualFinishMessageForServer;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.preparePostponeJobMessageForClient;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareSplitJobMessageForClient;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_CNA;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_SCHEDULER;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_SERVER;
import static com.greencloud.application.messages.fixtures.Fixtures.buildAdjustedJobContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildClientJob;
import static com.greencloud.application.messages.fixtures.Fixtures.buildClientJobContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildClientJobPart;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstance;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstanceContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobParts;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobPartsContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobStatusUpdate;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobStatusUpdateContent;
import static com.greencloud.application.utils.TimeUtils.useMockTime;
import static jade.lang.acl.ACLMessage.CANCEL;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.server.ServerAgent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class JobStatusMessageFactoryUnitTest {

	@BeforeEach
	void init() {
		useMockTime(parse("2022-01-01T13:30:00.000Z"), ZoneId.of("UTC"));
	}

	@Test
	@DisplayName("Test prepare job announcement message")
	void testPrepareJobAnnouncementMessage() {
		// given
		var mockJob = buildClientJob();
		var expectedContent = buildClientJobContent();

		// when
		final ACLMessage result = prepareJobAnnouncementMessage(TEST_SCHEDULER, mockJob);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(ANNOUNCED_JOB_PROTOCOL);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SCHEDULER));
	}

	@Test
	@DisplayName("Test prepare job cancellation message")
	void testPrepareJobCancellationMessage() {
		// given
		var receiver1 = new AID("test_receiver1", AID.ISGUID);
		var receiver2 = new AID("test_receiver2", AID.ISGUID);

		// when
		final ACLMessage result = prepareJobCancellationMessage("1", receiver1, receiver2);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CANCEL_JOB_PROTOCOL);
		assertThat(result.getPerformative()).isEqualTo(CANCEL);
		assertThat(result.getContent()).isEqualTo("1");
		assertThat(receiverIt).isNotEmpty().containsExactlyInAnyOrder(receiver1, receiver2);
	}

	@Test
	@DisplayName("Test prepare job status message for Scheduler")
	void testPrepareJobStatusMessageForScheduler() {
		// given
		var jobStatusUpdate = buildJobStatusUpdate();
		var conversationId = FINISH_JOB_ID;
		var expectedResult = buildJobStatusUpdateContent();

		var mockCloudNetwork = mock(CloudNetworkAgent.class);
		doReturn(TEST_SCHEDULER).when(mockCloudNetwork).getScheduler();

		// when
		final ACLMessage result = prepareJobStatusMessageForScheduler(mockCloudNetwork, jobStatusUpdate,
				conversationId);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(conversationId);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> "test_scheduler".equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job status message for Client with ClientJob as content")
	void testPrepareJobStatusMessageForClientWithClientJob() {
		// given
		var job = buildClientJob();
		var conversationId = STARTED_JOB_ID;
		var expectedResult = buildJobStatusUpdateContent();

		// when
		var result = prepareJobStatusMessageForClient(job, conversationId);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(conversationId);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> "test_client".equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job status message for Client with JobStatusUpdate as content")
	void testPrepareJobStatusMessageForClientWithJobStatusUpdate() {
		// given
		var jobUpdate = buildJobStatusUpdate();
		var conversationId = STARTED_JOB_ID;
		var expectedResult = buildJobStatusUpdateContent();

		// when
		var result = prepareJobStatusMessageForClient("test_client", jobUpdate, conversationId);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(conversationId);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> "test_client".equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare split job status message for Client")
	void testPrepareSplitJobMessageForClient() {
		// given
		var jobParts = buildJobParts();
		var expectedResult = buildJobPartsContent();

		// when
		var result = prepareSplitJobMessageForClient("test_client", jobParts);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(SPLIT_JOB_ID);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> "test_client".equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare postpone job message for client")
	void testPreparePostponeJobMessageForClient() {
		// given
		var jobPart = buildClientJobPart();
		var expectedContent = "1#part1";

		// when
		final ACLMessage result = preparePostponeJobMessageForClient(jobPart);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(POSTPONED_JOB_ID);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> "test_client".equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job adjustment message")
	void testPrepareJobAdjustmentMessage() {
		// given
		var adjustedJob = buildClientJob();
		var expectedResult = buildAdjustedJobContent();

		// when
		final ACLMessage result = prepareJobAdjustmentMessage("test_client", adjustedJob);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(RE_SCHEDULED_JOB_ID);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).anyMatch(aid1 -> "test_client".equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job status message for CNA")
	void testPrepareJobStatusMessageForCNA() {
		// given
		var jobInstance = buildJobInstance();
		var expectedResult = buildJobStatusUpdateContent();

		var server = mock(ServerAgent.class);
		doReturn(TEST_CNA).when(server).getOwnerCloudNetworkAgent();

		// when
		final ACLMessage result = prepareJobStatusMessageForCNA(jobInstance, DELAYED_JOB_ID, server);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(DELAYED_JOB_ID);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> TEST_CNA.getName().equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job status message for CNA and failed job")
	void testPrepareJobStatusMessageForCNAFailedJob() {
		// given
		var jobInstance = buildJobInstance();
		var expectedResult = buildJobStatusUpdateContent();

		var server = mock(ServerAgent.class);
		doReturn(TEST_CNA).when(server).getOwnerCloudNetworkAgent();

		// when
		final ACLMessage result = prepareJobStatusMessageForCNA(jobInstance, FAILED_JOB_ID, server);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(FAILED_JOB_PROTOCOL);
		assertThat(result.getPerformative()).isEqualTo(FAILURE);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> TEST_CNA.getName().equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job status finish message")
	void testPrepareJobFinishMessage() {
		// given
		var job = buildClientJob();
		var expectedResult = buildJobStatusUpdateContent();

		// when
		final ACLMessage result = prepareJobFinishMessage(job, TEST_CNA);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(FINISH_JOB_ID);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> TEST_CNA.getName().equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job status started message")
	void testPrepareJobStartedMessage() {
		// given
		var job = buildClientJob();
		var expectedResult = buildJobStatusUpdateContent();

		// when
		final ACLMessage result = prepareJobStartedMessage(job, TEST_CNA);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getProtocol()).isEqualTo(CHANGE_JOB_STATUS_PROTOCOL);
		assertThat(result.getConversationId()).isEqualTo(STARTED_JOB_ID);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).isNotEmpty().allMatch(aid1 -> TEST_CNA.getName().equals(aid1.getName()));
	}

	@Test
	@DisplayName("Test prepare job manual finish message")
	void testPrepareJobManualFinishMessage() {
		//given
		var jobInstance = buildJobInstance();
		var expectedResult = buildJobInstanceContent();

		// when
		final ACLMessage result = prepareManualFinishMessageForServer(jobInstance, TEST_SERVER);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// given
		assertThat(result.getProtocol()).isEqualTo(MANUAL_JOB_FINISH_PROTOCOL);
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo(expectedResult);
		assertThat(receiverIt).anyMatch(aid1 -> TEST_SERVER.getName().equals(aid1.getName()));
	}
}

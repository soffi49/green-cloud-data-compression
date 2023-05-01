package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.constants.MessageProtocolConstants.CONFIRMED_TRANSFER_PROTOCOL;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareAcceptJobOfferReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareFailureReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareInformReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.messages.fixtures.Fixtures.TEST_SERVER;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstance;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstanceContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildJobWithProtocolContent;
import static com.greencloud.application.messages.fixtures.Fixtures.buildRequest;
import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.AGREE;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.JobInstanceIdentifier;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class ReplyMessageFactoryUnitTest {

	@Test
	@DisplayName("Test prepare reply")
	void testPrepareReply() {
		// given
		var performative = REFUSE;
		var content = buildJobInstance();
		var expectedContent = buildJobInstanceContent();

		// when
		var result = prepareReply(buildRequest(), content, performative);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(performative);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo("test_protocol");
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare reply with protocol")
	void testPrepareReplyWithProtocol() {
		// given
		var performative = REFUSE;
		var protocol = "response_protocol";
		var content = buildJobInstance();
		var expectedContent = buildJobInstanceContent();

		// when
		var result = prepareReply(buildRequest(), content, performative, protocol);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(performative);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare string reply")
	void testPrepareStringReply() {
		// given
		final int performative = AGREE;
		final String content = "test_content";

		// when
		var result = prepareStringReply(buildRequest(), content, performative);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(performative);
		assertThat(result.getContent()).isEqualTo(content);
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo("test_protocol");
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare refuse reply")
	void testPrepareRefuseReply() {
		// when
		var result = prepareRefuseReply(buildRequest());
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(REFUSE);
		assertThat(result.getContent()).isEqualTo("REFUSE");
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo("test_protocol");
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare failure reply with default content")
	void testPrepareFailureNoContentReply() {
		// when
		var result = prepareFailureReply(buildRequest());
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(FAILURE);
		assertThat(result.getContent()).isEqualTo("FAILURE");
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo("test_protocol");
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare failure reply message")
	void testPrepareFailureReply() {
		// given
		var protocol = "response_protocol";
		var content = buildJobInstance();
		var expectedContent = buildJobInstanceContent();

		// when
		var result = prepareFailureReply(buildRequest(), content, protocol);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		assertThat(result.getPerformative()).isEqualTo(FAILURE);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo(protocol);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare inform reply")
	void testPrepareInformReply() {
		// when
		var result = prepareInformReply(buildRequest());
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(INFORM);
		assertThat(result.getContent()).isEqualTo("INFORM");
		assertThat(result.getConversationId()).isEqualTo("test_conversationId");
		assertThat(result.getProtocol()).isEqualTo("test_protocol");
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}

	@Test
	@DisplayName("Test prepare accept job offer reply")
	void testPrepareAcceptJobOfferReply() {
		// given
		var message = new ACLMessage(PROPOSE);
		message.setSender(TEST_SERVER);
		message.setProtocol(SERVER_JOB_CFP_PROTOCOL);
		message.setReplyWith("P1671062222360_1");
		message.setConversationId("C805691330_Server_1671062222359_0");

		var jobInstance = buildJobInstance();
		var protocol = CONFIRMED_TRANSFER_PROTOCOL;
		var expectedContent = buildJobWithProtocolContent(protocol);

		// when
		var result = prepareAcceptJobOfferReply(message, jobInstance, protocol);
		final Iterable<AID> receiverIt = result::getAllReceiver;

		// then
		assertThat(result.getPerformative()).isEqualTo(ACCEPT_PROPOSAL);
		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(result.getInReplyTo()).isEqualTo("P1671062222360_1");
		assertThat(result.getConversationId()).isEqualTo("C805691330_Server_1671062222359_0");
		assertThat(result.getProtocol()).isEqualTo(SERVER_JOB_CFP_PROTOCOL);
		assertThat(receiverIt).isNotEmpty().allMatch(aid -> aid.equals(TEST_SERVER));
	}
}

package com.greencloud.application.utils;

import static com.greencloud.application.messages.fixtures.Fixtures.buildJobInstance;
import static com.greencloud.application.utils.MessagingUtils.readMessageContent;
import static com.greencloud.application.utils.MessagingUtils.retrieveForPerformative;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.message.MessageBuilder;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class MessagingUtilsUnitTest {

	@Test
	@DisplayName("Test retrieve empty set of messages for performative")
	void testRetrieveForPerformativeEmpty() {
		assertThat(retrieveForPerformative(new Vector<>(), INFORM)).isEmpty();
	}

	@Test
	@DisplayName("Test retrieve non empty set of messages for performative")
	void testRetrieveForPerformative() {
		// given
		var messages = new Vector<>(prepareMessages());
		var expectedCondition = new Condition<ACLMessage>(message ->
				Objects.equals("Message 3", message.getContent()), "thirdMsg");

		// when
		var result = retrieveForPerformative(messages, REFUSE);

		// then
		assertThat(result).hasSize(1).areExactly(1, expectedCondition);
	}

	@Test
	@DisplayName("Test read message content - successful")
	void testReadMessageContent() {
		// given
		var jobInstance = buildJobInstance();
		var msg = MessageBuilder.builder()
				.withPerformative(PROPOSE)
				.withObjectContent(jobInstance)
				.build();

		// when
		var result = readMessageContent(msg, JobInstanceIdentifier.class);

		// then
		assertThat(result).isEqualTo(jobInstance);
	}

	@Test
	@DisplayName("Test read message content (unsuccessful)")
	void testReadMessageContentInvalid() throws JsonProcessingException {
		// given
		var jobInstance = buildJobInstance();
		var msg = MessageBuilder.builder()
				.withPerformative(PROPOSE)
				.withObjectContent(jobInstance)
				.build();

		// when & then
		assertThatThrownBy(() -> readMessageContent(msg, PowerJob.class))
				.isInstanceOf(IncorrectMessageContentException.class);
	}

	private List<ACLMessage> prepareMessages() {
		var aid1 = new AID("Sender1", AID.ISGUID);
		var aid2 = new AID("Sender2", AID.ISGUID);
		var aid3 = new AID("Sender3", AID.ISGUID);

		final ACLMessage aclMessage1 = new ACLMessage(PROPOSE);
		aclMessage1.setContent("Message 1");
		aclMessage1.setSender(aid1);
		final ACLMessage aclMessage2 = new ACLMessage(PROPOSE);
		aclMessage2.setContent("Message 2");
		aclMessage1.setSender(aid2);
		final ACLMessage aclMessage3 = new ACLMessage(REFUSE);
		aclMessage3.setContent("Message 3");
		aclMessage1.setSender(aid3);

		return List.of(aclMessage1, aclMessage2, aclMessage3);
	}
}

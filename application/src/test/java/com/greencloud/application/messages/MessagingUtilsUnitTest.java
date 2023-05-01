package com.greencloud.application.messages;

import static com.greencloud.application.mapper.JsonMapper.getMapper;
import static com.greencloud.application.utils.MessagingUtils.readMessageContent;
import static com.greencloud.application.utils.MessagingUtils.retrieveForPerformative;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.application.utils.MessagingUtils;
import com.greencloud.commons.domain.job.ImmutablePowerJob;
import com.greencloud.commons.domain.job.PowerJob;

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
		final Vector<ACLMessage> messages = new Vector<>(prepareMessages());

		assertThat(retrieveForPerformative(messages, REFUSE))
				.hasSize(1)
				.areExactly(1,
						new Condition<>(message -> Objects.equals("Message 3", message.getContent()),
								"thirdMsg"));
	}

	@Test
	@DisplayName("Test read message content (successful)")
	void testReadMessageContent() throws JsonProcessingException {
		final JobInstanceIdentifier jobInstance = ImmutableJobInstanceIdentifier.builder()
				.jobId("1")
				.startTime(parse("2022-01-01T11:00:00.000Z"))
				.build();
		final ACLMessage msg = new ACLMessage(PROPOSE);
		msg.setContent(getMapper().writeValueAsString(jobInstance));

		assertThat(MessagingUtils.readMessageContent(msg, JobInstanceIdentifier.class))
				.isEqualTo(jobInstance);
	}

	@Test
	@DisplayName("Test read message content (unsuccessful)")
	void testReadMessageContentInvalid() throws JsonProcessingException {
		final JobInstanceIdentifier jobInstance = ImmutableJobInstanceIdentifier.builder()
				.jobId("1")
				.startTime(parse("2022-01-01T11:00:00.000Z"))
				.build();
		final ACLMessage msg = new ACLMessage(PROPOSE);
		msg.setContent(getMapper().writeValueAsString(jobInstance));

		assertThatThrownBy(() -> MessagingUtils.readMessageContent(msg, PowerJob.class))
				.isInstanceOf(IncorrectMessageContentException.class);
	}

	private List<ACLMessage> prepareMessages() {
		final AID aid1 = mock(AID.class);
		final AID aid2 = mock(AID.class);
		final AID aid3 = mock(AID.class);

		doReturn("Sender1").when(aid1).getName();
		doReturn("Sender2").when(aid2).getName();
		doReturn("Sender3").when(aid3).getName();

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

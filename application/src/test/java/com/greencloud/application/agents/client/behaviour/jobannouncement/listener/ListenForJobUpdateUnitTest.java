package com.greencloud.application.agents.client.behaviour.jobannouncement.listener;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.listener.logs.JobAnnouncementListenerLog.CLIENT_JOB_ON_HOLD_LOG;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.listener.logs.JobAnnouncementListenerLog.CLIENT_UNKNOWN_UPDATE_LOG;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.listener.templates.JobAnnouncementMessageTemplates.CLIENT_JOB_UPDATE_TEMPLATE;
import static com.greencloud.application.messages.constants.MessageConversationConstants.BACK_UP_POWER_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.DELAYED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.FAILED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.FINISH_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.ON_HOLD_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.POSTPONED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.PROCESSING_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.RE_SCHEDULED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.SCHEDULED_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.SPLIT_JOB_ID;
import static com.greencloud.application.messages.constants.MessageConversationConstants.STARTED_JOB_ID;
import static jade.lang.acl.ACLMessage.INFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandleGenericJobStatusUpdate;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandleJobFailedUpdate;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandleJobFinishUpdate;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandleJobSplitUpdate;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandleJobStartUpdate;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandlePostponeJobUpdate;
import com.greencloud.application.agents.client.behaviour.jobannouncement.handler.HandleRescheduleJobUpdate;
import com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum;
import com.greencloud.commons.message.MessageBuilder;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class ListenForJobUpdateUnitTest {

	@Mock
	private static ClientAgent mockClientAgent;

	private static ListenForJobUpdate testBehaviour;

	static private Stream<Arguments> updateHandlerTest() {
		return Stream.of(
				arguments(SCHEDULED_JOB_ID, HandleGenericJobStatusUpdate.class, ClientJobUpdateEnum.SCHEDULED_JOB_ID),
				arguments(PROCESSING_JOB_ID, HandleGenericJobStatusUpdate.class, ClientJobUpdateEnum.PROCESSING_JOB_ID),
				arguments(DELAYED_JOB_ID, HandleGenericJobStatusUpdate.class, ClientJobUpdateEnum.DELAYED_JOB_ID),
				arguments(BACK_UP_POWER_JOB_ID, HandleGenericJobStatusUpdate.class,
						ClientJobUpdateEnum.BACK_UP_POWER_JOB_ID),
				arguments(GREEN_POWER_JOB_ID, HandleGenericJobStatusUpdate.class,
						ClientJobUpdateEnum.GREEN_POWER_JOB_ID),
				arguments(ON_HOLD_JOB_ID, HandleGenericJobStatusUpdate.class, ClientJobUpdateEnum.ON_HOLD_JOB_ID),
				arguments(STARTED_JOB_ID, HandleJobStartUpdate.class, ClientJobUpdateEnum.STARTED_JOB_ID),
				arguments(FINISH_JOB_ID, HandleJobFinishUpdate.class, ClientJobUpdateEnum.FINISH_JOB_ID),
				arguments(FAILED_JOB_ID, HandleJobFailedUpdate.class, ClientJobUpdateEnum.FAILED_JOB_ID),
				arguments(POSTPONED_JOB_ID, HandlePostponeJobUpdate.class, ClientJobUpdateEnum.POSTPONED_JOB_ID),
				arguments(SPLIT_JOB_ID, HandleJobSplitUpdate.class, ClientJobUpdateEnum.SPLIT_JOB_ID),
				arguments(RE_SCHEDULED_JOB_ID, HandleRescheduleJobUpdate.class, ClientJobUpdateEnum.RE_SCHEDULED_JOB_ID)
		);
	}

	@BeforeEach
	void setUp() {
		testBehaviour = new ListenForJobUpdate(mockClientAgent);
	}

	@ParameterizedTest
	@MethodSource("updateHandlerTest")
	@DisplayName("Test get job update handler")
	void testGetUpdateHandler(final String type, final Class<?> expectedHandler, final ClientJobUpdateEnum updateEnum) {
		// given
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withConversationId(type)
				.build();

		// when
		var handler = testBehaviour.getUpdateHandler(message, updateEnum);

		// then
		assertThat(handler).isInstanceOf(expectedHandler);
	}

	@Test
	@DisplayName("Test receive message for unknown handler type")
	void testActionUnknownHandler() {
		// given
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withConversationId("UNKNOWN_TYPE")
				.build();
		var expectedLog = CLIENT_UNKNOWN_UPDATE_LOG.replace("{}", "UNKNOWN_TYPE");

		doReturn(message).when(mockClientAgent).receive(CLIENT_JOB_UPDATE_TEMPLATE);

		// prepare
		var logCaptor = LogCaptor.forClass(ListenForJobUpdate.class);
		logCaptor.setLogLevelToInfo();

		// when
		testBehaviour.action();

		// then
		assertThat(logCaptor.getInfoLogs()).containsExactly(expectedLog);
	}

	@Test
	@DisplayName("Test receive message of known type with handler message")
	void testAction() {
		// given
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withConversationId(ON_HOLD_JOB_ID)
				.build();
		doReturn(message).when(mockClientAgent).receive(CLIENT_JOB_UPDATE_TEMPLATE);

		// prepare
		var logCaptor = LogCaptor.forClass(ListenForJobUpdate.class);
		logCaptor.setLogLevelToInfo();

		// when
		testBehaviour.action();

		// then
		verify(mockClientAgent).addBehaviour(argThat(behaviour -> behaviour instanceof HandleGenericJobStatusUpdate));
		assertThat(logCaptor.getInfoLogs()).containsExactly(CLIENT_JOB_ON_HOLD_LOG);
	}

	@Test
	@DisplayName("Test receive message of known type without handler message")
	void testActionNoLogMessage() {
		// given
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withConversationId(STARTED_JOB_ID)
				.build();
		doReturn(message).when(mockClientAgent).receive(CLIENT_JOB_UPDATE_TEMPLATE);

		// prepare
		var logCaptor = LogCaptor.forClass(ListenForJobUpdate.class);
		logCaptor.setLogLevelToInfo();

		// when
		testBehaviour.action();

		// then
		verify(mockClientAgent).addBehaviour(argThat(behaviour -> behaviour instanceof HandleJobStartUpdate));
		assertThat(logCaptor.getInfoLogs()).isEmpty();
	}
}

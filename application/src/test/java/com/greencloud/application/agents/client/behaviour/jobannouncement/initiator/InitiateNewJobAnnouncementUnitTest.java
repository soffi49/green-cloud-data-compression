package com.greencloud.application.agents.client.behaviour.jobannouncement.initiator;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.buildJobExecutionInstance;
import static com.greencloud.application.agents.client.constants.ClientAgentConstants.SCHEDULER_AGENT;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.ANNOUNCED_JOB_PROTOCOL;
import static com.greencloud.application.messages.factory.JobStatusMessageFactory.prepareJobAnnouncementMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.domain.ClientJobExecution;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class InitiateNewJobAnnouncementUnitTest {

	private static final AID scheduler = new AID("test_scheduler", AID.ISGUID);
	private static final ClientJobExecution job = buildJobExecutionInstance("1");

	@Mock
	private static ClientAgent mockClientAgent;

	private static InitiateNewJobAnnouncement testBehaviour;

	@BeforeEach
	void setUp() {
		mockClientAgent = spy(ClientAgent.class);
		doReturn(job).when(mockClientAgent).getJobExecution();
		testBehaviour = new InitiateNewJobAnnouncement(mockClientAgent);

		var parentBehaviour = new SequentialBehaviour();
		parentBehaviour.addSubBehaviour(testBehaviour);
		parentBehaviour.getDataStore().put(SCHEDULER_AGENT, scheduler);
	}

	@Test
	@DisplayName("Test behaviour action")
	void testAction() {
		// when
		testBehaviour.action();

		// then
		verify(mockClientAgent).send(argThat(message -> message.getProtocol().equals(ANNOUNCED_JOB_PROTOCOL)));
	}
}

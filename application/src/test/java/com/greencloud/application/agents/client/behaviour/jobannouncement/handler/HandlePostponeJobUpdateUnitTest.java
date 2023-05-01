package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.setUpClient;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.POSTPONED_JOB_ID;
import static com.greencloud.application.utils.TimeUtils.setSystemStartTime;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
import com.greencloud.application.agents.client.management.ClientManagement;
import com.greencloud.commons.message.MessageBuilder;
import com.gui.agents.ClientAgentNode;
import com.gui.controller.GuiController;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class HandlePostponeJobUpdateUnitTest {

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static ClientAgentNode mockClientNode;
	@Mock
	private static ClientManagement mockClientManagement;
	@Mock
	private static GuiController mockGuiController;

	@Mock
	private static HandlePostponeJobUpdate testBehaviour;

	@BeforeEach
	void setUp() {
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withStringContent("1#part1")
				.build();

		mockClientAgent = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		mockClientAgent.setAgentNode(mockClientNode);
		doReturn(mockGuiController).when(mockClientAgent).getGuiController();

		mockClientManagement = spy(new ClientManagement(mockClientAgent));
		doReturn(mockClientManagement).when(mockClientAgent).manage();

		testBehaviour = spy(new HandlePostponeJobUpdate(message, mockClientAgent, POSTPONED_JOB_ID));
		doNothing().when(mockClientAgent).writeMonitoringData(any(), any());
	}

	@Test
	@DisplayName("Test behaviour action for split job")
	void testActionSplitJob() {
		// given
		var postponeStart = parse("2022-01-01T11:30:00.833Z");
		var postponeEnd = parse("2022-01-01T12:30:00.833Z");

		setSystemStartTime(parse("2022-01-01T11:30:00.000Z"));
		doReturn(true).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(testBehaviour).readjustJobPartTimeFrames("1#part1", postponeStart, postponeEnd);
		verify(mockClientNode).updateJobTimeFrame(any(), any(), eq("1#part1"));

		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobSimulatedStart()).isEqualTo(postponeStart);
		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobSimulatedEnd()).isEqualTo(postponeEnd);
	}

	@Test
	@DisplayName("Test behaviour action for job with no split")
	void testActionNoSplit() {
		// given
		var postponeStart = parse("2022-01-01T11:30:00.833Z");
		var postponeEnd = parse("2022-01-01T12:30:00.833Z");

		setSystemStartTime(parse("2022-01-01T11:30:00.000Z"));
		doReturn(false).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(testBehaviour).readjustJobTimeFrames(postponeStart, postponeEnd);
		verify(mockClientNode).updateJobTimeFrame(any(), any());

		assertThat(mockClientAgent.getJobExecution().getJobSimulatedStart()).isEqualTo(postponeStart);
		assertThat(mockClientAgent.getJobExecution().getJobSimulatedEnd()).isEqualTo(postponeEnd);
	}
}

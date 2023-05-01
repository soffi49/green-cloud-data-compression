package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.fixtures.Fixtures.buildJobParts;
import static com.greencloud.application.agents.client.fixtures.Fixtures.setUpClient;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.SPLIT_JOB_ID;
import static com.greencloud.application.utils.TimeUtils.setSystemStartTime;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.time.Instant.parse;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.management.ClientManagement;
import com.greencloud.application.domain.job.JobParts;
import com.greencloud.commons.message.MessageBuilder;
import com.gui.agents.ClientAgentNode;
import com.gui.controller.GuiController;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class HandleJobSplitUpdateUnitTest {

	private static final JobParts JOB_PARTS = buildJobParts();

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static ClientAgentNode mockClientNode;
	@Mock
	private static ClientManagement mockClientManagement;
	@Mock
	private static GuiController mockGuiController;

	@Mock
	private static HandleJobSplitUpdate testBehaviour;

	@BeforeEach
	void setUp() {
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withObjectContent(JOB_PARTS)
				.build();

		mockClientAgent = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		mockClientAgent.setAgentNode(mockClientNode);
		doReturn(mockGuiController).when(mockClientAgent).getGuiController();

		mockClientManagement = spy(new ClientManagement(mockClientAgent));
		doReturn(mockClientManagement).when(mockClientAgent).manage();

		testBehaviour = spy(new HandleJobSplitUpdate(message, mockClientAgent, SPLIT_JOB_ID));
		doNothing().when(mockClientAgent).writeMonitoringData(any(), any());
	}

	@Test
	@DisplayName("Test behaviour action")
	void testAction() {
		// given
		setSystemStartTime(parse("2022-01-01T11:30:00.000Z"));
		doReturn(new ConcurrentHashMap<>(emptyMap())).when(mockClientAgent).getJobParts();

		// when
		testBehaviour.action();

		// then
		verify(mockClientNode).informAboutSplitJob(anyList());

		assertThat(mockClientAgent.isSplit()).isTrue();
		assertThat(mockClientAgent.getJobParts())
				.as("Has correct size")
				.hasSize(2)
				.as("Has correct content")
				.allSatisfy((id, clientJob) -> {
					assertThat(clientJob.getJobStatus()).isEqualTo(CREATED);
					assertThat(clientJob.getJobSimulatedStart()).isEqualTo(parse("2022-01-01T11:30:00.000Z"));
					assertThat(clientJob.getJobSimulatedDeadline()).isEqualTo(parse("2022-01-01T13:30:00.000Z"));
					assertThat(clientJob.getJobSimulatedEnd()).isEqualTo(parse("2022-01-01T12:30:00.000Z"));
				})
				.hasEntrySatisfying("1#part1",
						clientJob -> assertThat(clientJob.getJob()).isEqualTo(JOB_PARTS.getJobParts().get(0)))
				.hasEntrySatisfying("1#part2",
						clientJob -> assertThat(clientJob.getJob()).isEqualTo(JOB_PARTS.getJobParts().get(1)));
	}
}

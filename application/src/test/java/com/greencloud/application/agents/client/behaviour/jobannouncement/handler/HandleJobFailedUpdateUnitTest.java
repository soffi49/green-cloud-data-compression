package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.FAILED_JOB_ID;
import static com.greencloud.application.agents.client.fixtures.Fixtures.buildJobStatusUpdate;
import static com.greencloud.application.agents.client.fixtures.Fixtures.setUpClient;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.FAILED;
import static jade.lang.acl.ACLMessage.INFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.management.ClientManagement;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.message.MessageBuilder;
import com.gui.agents.ClientAgentNode;
import com.gui.controller.GuiController;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class HandleJobFailedUpdateUnitTest {

	private static final JobStatusUpdate JOB_STATUS = buildJobStatusUpdate("1#part1");

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static ClientAgentNode mockClientNode;
	@Mock
	private static ClientManagement mockClientManagement;
	@Mock
	private static GuiController mockGuiController;

	@Mock
	private static HandleJobFailedUpdate testBehaviour;

	@BeforeEach
	void setUp() {
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withObjectContent(JOB_STATUS)
				.build();

		mockClientAgent = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		mockClientAgent.setAgentNode(mockClientNode);
		doReturn(mockGuiController).when(mockClientAgent).getGuiController();

		mockClientManagement = spy(new ClientManagement(mockClientAgent));
		doReturn(mockClientManagement).when(mockClientAgent).manage();

		testBehaviour = spy(new HandleJobFailedUpdate(message, mockClientAgent, FAILED_JOB_ID));
		doNothing().when(mockClientAgent).writeMonitoringData(any(), any());
		doNothing().when(mockClientAgent).doDelete();
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	@DisplayName("Test behaviour action")
	void testAction(final boolean isSplit) {
		// given
		doReturn(isSplit).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(mockClientNode).updateJobStatus(FAILED);
		verify(mockGuiController).updateFailedJobsCountByValue(1);
		verify(mockClientManagement).writeClientData(true);
		verify(mockClientAgent).doDelete();

		assertThat(mockClientAgent.getJobParts()).containsKey("1#part1");
		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobStatus()).isEqualTo(CREATED);
		assertThat(mockClientAgent.getJobExecution().getJobStatus()).isEqualTo(FAILED);
	}
}

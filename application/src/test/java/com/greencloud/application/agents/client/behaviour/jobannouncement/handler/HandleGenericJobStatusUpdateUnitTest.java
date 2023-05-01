package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.buildJobStatusUpdate;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.setUpClient;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.ON_HOLD_JOB_ID;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.ON_HOLD;
import static jade.lang.acl.ACLMessage.INFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.management.ClientManagement;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.message.MessageBuilder;
import com.gui.agents.ClientAgentNode;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class HandleGenericJobStatusUpdateUnitTest {

	private static final JobStatusUpdate JOB_STATUS = buildJobStatusUpdate("1#part1");

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static ClientAgentNode mockClientNode;
	@Mock
	private static ClientManagement mockClientManagement;

	@Mock
	private static HandleGenericJobStatusUpdate testBehaviour;

	static private Stream<Arguments> actionTest() {
		return Stream.of(arguments(true, 0, 1), arguments(false, 1, 0));
	}

	@BeforeEach
	void setUp() {
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withObjectContent(JOB_STATUS)
				.build();

		mockClientAgent = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		mockClientAgent.setAgentNode(mockClientNode);

		mockClientManagement = spy(new ClientManagement(mockClientAgent));
		doReturn(mockClientManagement).when(mockClientAgent).manage();

		testBehaviour = spy(new HandleGenericJobStatusUpdate(message, mockClientAgent, ON_HOLD_JOB_ID));
		doNothing().when(mockClientAgent).writeMonitoringData(any(), any());
	}

	@ParameterizedTest
	@MethodSource("actionTest")
	@DisplayName("Test behaviour action")
	void testAction(final boolean isSplit, final int timesOriginalUpdate, final int timesPartsUpdate) {
		// given
		var jobPartStatus = !isSplit ? CREATED : ON_HOLD;
		doReturn(isSplit).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(testBehaviour, times(timesOriginalUpdate)).updateInformationOfJobStatusUpdate(JOB_STATUS);
		verify(testBehaviour, times(timesPartsUpdate)).updateInformationOfJobPartStatusUpdate(JOB_STATUS);

		assertThat(mockClientAgent.getJobParts()).containsKey("1#part1");
		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobStatus()).isEqualTo(jobPartStatus);
		assertThat(mockClientAgent.getJobExecution().getJobStatus()).isEqualTo(ON_HOLD);
	}
}

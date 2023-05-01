package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.buildJobStatusUpdate;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.setUpClient;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.FAILED_JOB_ID;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.FINISH_JOB_ID;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.PROCESSING_JOB_ID;
import static com.greencloud.application.utils.TimeUtils.setSystemStartTime;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.PROCESSED;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.management.ClientManagement;
import com.gui.agents.ClientAgentNode;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class AbstractJobUpdateHandlerUnitTest {

	@Spy
	private static AbstractJobUpdateHandler testBehaviour;

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static ClientAgentNode mockClientNode;
	@Mock
	private static ClientManagement mockClientManagement;

	static private Stream<Arguments> onEndTest() {
		var clientMock = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		clientMock.setAgentNode(mockClientNode);

		mockClientManagement = spy(new ClientManagement(clientMock));
		doReturn(mockClientManagement).when(clientMock).manage();

		doNothing().when(clientMock).writeMonitoringData(any(), any());

		return Stream.of(
				arguments(clientMock, new HandleJobFinishUpdate(null, clientMock, FINISH_JOB_ID), 0),
				arguments(clientMock, new HandleJobFailedUpdate(null, clientMock, FAILED_JOB_ID), 0),
				arguments(clientMock, new HandleJobStartUpdate(null, clientMock, FAILED_JOB_ID), 1)
		);
	}

	@BeforeEach
	void setUp() {
		mockClientAgent = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		mockClientAgent.setAgentNode(mockClientNode);

		mockClientManagement = spy(new ClientManagement(mockClientAgent));
		doReturn(mockClientManagement).when(mockClientAgent).manage();

		testBehaviour = spy(new AbstractJobUpdateHandler(null, mockClientAgent, PROCESSING_JOB_ID) {
			@Override
			public void action() {
			}
		});
		doNothing().when(mockClientAgent).writeMonitoringData(any(), any());
	}

	@Test
	@DisplayName("Test update information of job status")
	void testUpdateInformationOfJobStatusUpdate() {
		// given
		var jobStatusUpdate = buildJobStatusUpdate("1");

		// when
		testBehaviour.updateInformationOfJobStatusUpdate(jobStatusUpdate);

		// then
		verify(mockClientAgent).getAgentNode();
		assertThat(mockClientAgent.getJobExecution().getJobStatus()).isEqualTo(PROCESSED);
	}

	@Test
	@DisplayName("Test update information of job part status")
	void testUpdateInformationOfJobPartStatusUpdate() {
		// given
		var jobStatusUpdate = buildJobStatusUpdate("1#part1");

		// when
		testBehaviour.updateInformationOfJobPartStatusUpdate(jobStatusUpdate);

		// then
		verify(mockClientAgent).getAgentNode();
		verify(mockClientNode).updateJobStatus(PROCESSED, "1#part1");
		verify(mockClientManagement).updateOriginalJobStatus(PROCESSED);

		assertThat(mockClientAgent.getJobParts()).containsKey("1#part1");
		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobStatus()).isEqualTo(PROCESSED);
	}

	@Test
	@DisplayName("Test readjust job time frames")
	void testReadjustJobTimeFrames() {
		// given
		var newStart = parse("2022-01-01T10:00:00.000Z");
		var newEnd = parse("2022-01-01T10:00:00.000Z");
		setSystemStartTime(parse("2022-01-01T10:00:00.000Z"));

		// when
		testBehaviour.readjustJobTimeFrames(newStart, newEnd);

		// then
		verify(mockClientAgent).getAgentNode();
		verify(mockClientNode).updateJobTimeFrame(any(), any());

		assertThat(mockClientAgent.getJobExecution().getJobSimulatedStart()).isEqualTo(newStart);
		assertThat(mockClientAgent.getJobExecution().getJobSimulatedEnd()).isEqualTo(newEnd);
	}

	@Test
	@DisplayName("Test readjust job part time frames")
	void testReadjustJobPartTimeFrames() {
		// given
		var jobPart = "1#part1";
		var newStart = parse("2022-01-01T10:00:00.000Z");
		var newEnd = parse("2022-01-01T10:00:00.000Z");
		setSystemStartTime(parse("2022-01-01T10:00:00.000Z"));

		// when
		testBehaviour.readjustJobPartTimeFrames(jobPart, newStart, newEnd);

		// then
		verify(mockClientAgent).getAgentNode();
		verify(mockClientNode).updateJobTimeFrame(any(), any(), eq(jobPart));

		assertThat(mockClientAgent.getJobParts().get(jobPart).getJobSimulatedStart()).isEqualTo(newStart);
		assertThat(mockClientAgent.getJobParts().get(jobPart).getJobSimulatedEnd()).isEqualTo(newEnd);
	}

	@ParameterizedTest
	@MethodSource("onEndTest")
	@DisplayName("Test on end action")
	void testOnEnd(final ClientAgent clientMock, final AbstractJobUpdateHandler handler, final int executionTimes) {
		// given
		testBehaviour = handler;

		// when
		testBehaviour.onEnd();

		// then
		verify(clientMock, times(executionTimes)).manage();
	}
}

package com.greencloud.application.agents.scheduler;

import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_DEADLINE_PRIORITY;
import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_POWER_PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import com.greencloud.application.agents.scheduler.managment.SchedulerAdaptationManagement;
import com.greencloud.application.agents.scheduler.managment.SchedulerStateManagement;
import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.gui.agents.SchedulerAgentNode;

class AbstractSchedulerAgentTest {

	SchedulerAgent schedulerAgent;
	SchedulerAgentNode schedulerAgentNode;
	@Spy
	private SchedulerAdaptationManagement mockAdaptationManagement;
	@Spy
	private SchedulerStateManagement mockStateManagement;

	@BeforeEach
	void init() {
		schedulerAgent = spy(SchedulerAgent.class);
		schedulerAgentNode = mock(SchedulerAgentNode.class);

		schedulerAgent.setMaximumQueueSize(10);
		schedulerAgent.setUpPriorityQueue();
		schedulerAgent.setDeadlinePriority(1);
		schedulerAgent.setPowerPriority(1);

		doReturn(schedulerAgentNode).when(schedulerAgent).getAgentNode();

		mockAdaptationManagement = spy(new SchedulerAdaptationManagement(schedulerAgent));
		mockStateManagement = spy(new SchedulerStateManagement(schedulerAgent));

		doReturn(mockAdaptationManagement).when(schedulerAgent).adapt();
		doReturn(mockStateManagement).when(schedulerAgent).manage();

		doNothing().when(mockStateManagement).updateJobQueueGUI();
	}

	@Test
	@DisplayName("Test executing adaptation action for incrementing deadline weight")
	void testExecuteIncreaseDeadline() {
		schedulerAgent.executeAction(INCREASE_DEADLINE_PRIORITY, null);

		assertThat(schedulerAgent.getDeadlinePriority()).isEqualTo(2);
	}

	@Test
	@DisplayName("Test executing adaptation action for incrementing power division weight")
	void testExecuteIncreasePowerDivision() {
		schedulerAgent.executeAction(INCREASE_POWER_PRIORITY, null);

		assertThat(schedulerAgent.getPowerPriority()).isEqualTo(2);
	}

	@Test
	@DisplayName("Test adding job to the queue - test deadline priority")
	void testAddJobToTheQueueDeadlinePriority() {
		var mockJob = ImmutableClientJob.builder()
				.jobId("1")
				.jobInstanceId("jobInstance1")
				.clientIdentifier("Client1")
				.clientAddress("client_address")
				.startTime(Instant.parse("2022-01-01T08:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T10:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T12:00:00.000Z"))
				.power(100)
				.build();
		var mockJob2 = ImmutableClientJob.builder()
				.jobId("2")
				.jobInstanceId("jobInstance2")
				.clientIdentifier("Client2")
				.clientAddress("client_address")
				.startTime(Instant.parse("2022-01-01T08:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T10:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T14:00:00.000Z"))
				.power(100)
				.build();

		schedulerAgent.getJobsToBeExecuted().put(mockJob2);
		schedulerAgent.getJobsToBeExecuted().put(mockJob);

		assertThat(schedulerAgent.getJobsToBeExecuted().peek()).isEqualTo(mockJob);
	}

	@Test
	@DisplayName("Test adding job to the queue - test power priority")
	void testAddJobToTheQueuePowerPriority() {
		var mockJob = ImmutableClientJob.builder()
				.jobId("1")
				.jobInstanceId("jobInstance1")
				.clientIdentifier("Client1")
				.clientAddress("client_address")
				.startTime(Instant.parse("2022-01-01T08:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T10:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T12:00:00.000Z"))
				.power(100)
				.build();
		var mockJob2 = ImmutableClientJob.builder()
				.jobId("2")
				.jobInstanceId("jobInstance2")
				.clientIdentifier("Client2")
				.clientAddress("client_address")
				.startTime(Instant.parse("2022-01-01T08:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T10:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T14:00:00.000Z"))
				.power(110)
				.build();

		schedulerAgent.getJobsToBeExecuted().put(mockJob2);
		schedulerAgent.getJobsToBeExecuted().put(mockJob);

		assertThat(schedulerAgent.getJobsToBeExecuted().peek()).isEqualTo(mockJob);
	}
}

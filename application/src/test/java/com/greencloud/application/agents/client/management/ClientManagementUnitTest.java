package com.greencloud.application.agents.client.management;

import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.DELAYED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.FINISHED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.ON_BACK_UP;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.ON_HOLD;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.PROCESSED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.SCHEDULED;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.assertj.core.api.Condition;
import org.assertj.core.data.Offset;
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
import com.greencloud.application.agents.client.domain.ClientJobExecution;
import com.greencloud.commons.domain.job.enums.JobClientStatusEnum;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class ClientManagementUnitTest {

	@Spy
	private static ClientJobExecution mockOriginalJob;

	@Mock
	private static ClientAgent mockClient;
	@Mock
	private static ClientManagement mockClientManagement;

	static private Stream<Arguments> jobStatusesIncorrect() {
		return Stream.of(
				arguments(SCHEDULED, SCHEDULED, CREATED, CREATED),
				arguments(FINISHED, IN_PROGRESS, FINISHED, IN_PROGRESS),
				arguments(PROCESSED, DELAYED, PROCESSED, IN_PROGRESS),
				arguments(DELAYED, IN_PROGRESS, PROCESSED, IN_PROGRESS),
				arguments(ON_BACK_UP, IN_PROGRESS, ON_HOLD, ON_HOLD),
				arguments(IN_PROGRESS, IN_PROGRESS, ON_BACK_UP, ON_BACK_UP)
		);
	}

	static private Stream<Arguments> jobStatusesCorrect() {
		return Stream.of(
				arguments(SCHEDULED, SCHEDULED, SCHEDULED, CREATED),
				arguments(FINISHED, FINISHED, FINISHED, IN_PROGRESS),
				arguments(PROCESSED, CREATED, PROCESSED, SCHEDULED),
				arguments(DELAYED, PROCESSED, PROCESSED, PROCESSED),
				arguments(ON_BACK_UP, IN_PROGRESS, IN_PROGRESS, IN_PROGRESS),
				arguments(ON_BACK_UP, PROCESSED, PROCESSED, PROCESSED),
				arguments(IN_PROGRESS, PROCESSED, PROCESSED, PROCESSED),
				arguments(IN_PROGRESS, IN_PROGRESS, IN_PROGRESS, PROCESSED),
				arguments(ON_HOLD, IN_PROGRESS, IN_PROGRESS, IN_PROGRESS)
		);
	}

	static private Stream<Arguments> partsCorrectness() {
		return Stream.of(
				arguments(SCHEDULED, SCHEDULED, SCHEDULED, true),
				arguments(FINISHED, FINISHED, SCHEDULED, false)
		);
	}

	@BeforeEach
	void setUp() {
		doReturn(mockOriginalJob).when(mockClient).getJobExecution();
		mockClientManagement = spy(new ClientManagement(mockClient));
	}

	@ParameterizedTest
	@MethodSource("jobStatusesIncorrect")
	@DisplayName("Test update original job status when update should not happen")
	void testUpdateOriginalJobStatusFail(JobClientStatusEnum newStatus, JobClientStatusEnum jobPart1Status,
			JobClientStatusEnum jobPart2Status, JobClientStatusEnum currentStatus) {
		// given
		mockOriginalJob.setJobStatus(currentStatus);
		doReturn(new ConcurrentHashMap<>(Map.of(
				"1#1", new ClientJobExecution(null, null, null, null, jobPart1Status),
				"1#2", new ClientJobExecution(null, null, null, null, jobPart2Status)
		))).when(mockClient).getJobParts();

		// when
		mockClientManagement.updateOriginalJobStatus(newStatus);

		// then
		assertThat(mockOriginalJob.getJobStatus()).isNotEqualTo(newStatus);
		assertThat(mockOriginalJob.getJobStatus()).isEqualTo(currentStatus);
		verify(mockClient, times(0)).getAgentNode();
	}

	@ParameterizedTest
	@MethodSource("jobStatusesCorrect")
	@DisplayName("Test update original job status when update should succeed")
	void testUpdateOriginalJobStatusSucceed(JobClientStatusEnum newStatus, JobClientStatusEnum jobPart1Status,
			JobClientStatusEnum jobPart2Status, JobClientStatusEnum currentStatus) {
		// given
		mockOriginalJob.setJobStatus(currentStatus);
		doReturn(new ConcurrentHashMap<>(Map.of(
				"1#1", new ClientJobExecution(null, null, null, null, jobPart1Status),
				"1#2", new ClientJobExecution(null, null, null, null, jobPart2Status)
		))).when(mockClient).getJobParts();

		// when
		mockClientManagement.updateOriginalJobStatus(newStatus);

		// then
		assertThat(mockOriginalJob.getJobStatus()).isEqualTo(newStatus);
		verify(mockClient, times(1)).getAgentNode();
	}

	@Test
	@DisplayName("Test update job status duration map")
	void testUpdateJobStatusDuration() {
		// given
		mockOriginalJob.setJobStatus(PROCESSED);
		mockOriginalJob.getTimer().startTimeMeasure(parse("2022-01-01T10:00:00.000Z"));
		mockOriginalJob.updateJobStatusDuration(IN_PROGRESS, parse("2022-01-01T10:00:02.000Z"));

		// when
		var result = mockOriginalJob.getJobDurationMap();
		var notProcessed = result.entrySet().stream()
				.filter(e -> !e.getKey().equals(PROCESSED))
				.map(Map.Entry::getValue).toList();

		// then
		assertThat(result).hasSize(10);
		assertThat(result.get(PROCESSED)).isCloseTo(2000, Offset.offset(50L));
		assertThat(notProcessed).are(new Condition<>(val -> val == 0L, "isZero"));
	}

	@ParameterizedTest
	@MethodSource("partsCorrectness")
	@DisplayName("Test check if all parts match given status")
	void testCheckIfAllPartsMatchStatus(JobClientStatusEnum statusToCheck, JobClientStatusEnum jobPart1Status,
			JobClientStatusEnum jobPart2Status, boolean result) {
		// given
		doReturn(new ConcurrentHashMap<>(Map.of(
				"1#1", new ClientJobExecution(null, null, null, null, jobPart1Status),
				"1#2", new ClientJobExecution(null, null, null, null, jobPart2Status)
		))).when(mockClient).getJobParts();

		// then
		assertThat(mockClientManagement.checkIfAllPartsMatchStatus(statusToCheck)).isEqualTo(result);
	}
}

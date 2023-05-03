package com.greencloud.application.agents.client.domain;

import static com.greencloud.application.agents.client.fixtures.Fixtures.TEST_CLIENT;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.IN_PROGRESS;
import static java.time.Instant.parse;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.greencloud.commons.domain.job.enums.JobClientStatusEnum;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class ClientJobExecutionUnitTest {

	@Spy
	private static ClientJobExecution mockClientJobExecution;

	@Test
	@DisplayName("Test ClientJobExecution constructor setting job params")
	void testClientJobExecutionConstructorJobParam() {
		// given
		var clientJob = ImmutableClientJob.builder()
				.jobId("1")
				.clientIdentifier("client1")
				.clientAddress("client_address")
				.jobInstanceId("jobInstance1")
				.deadline(parse("2022-01-01T13:30:00.000Z"))
				.endTime(parse("2022-01-01T12:30:00.000Z"))
				.startTime(parse("2022-01-01T11:30:00.000Z"))
				.power(10)
				.build();
		var jobStart = parse("2022-01-01T14:30:00.000Z");
		var jobEnd = parse("2022-01-01T14:31:00.000Z");
		var jobDeadline = parse("2022-01-01T14:32:00.000Z");

		// when
		var result = new ClientJobExecution(clientJob, jobStart, jobEnd, jobDeadline, CREATED);

		// then
		assertThat(result.getJobStatus()).isEqualTo(CREATED);
		assertThat(result.getJob()).isEqualTo(clientJob);
		assertThat(result.getJobSimulatedDeadline()).isEqualTo(jobDeadline);
		assertThat(result.getJobSimulatedEnd()).isEqualTo(jobEnd);
		assertThat(result.getJobSimulatedStart()).isEqualTo(jobStart);
		assertThat(result.getJobDurationMap())
				.hasSize(JobClientStatusEnum.values().length)
				.allSatisfy((status, val) -> assertThat(val).isZero());
	}

	@Test
	@DisplayName("Test ClientJobExecution constructor creating job")
	void testClientJobExecutionConstructorJob() {
		// given
		var jobId = "1";
		var power = 20;
		var jobStart = parse("2022-01-01T11:30:00.000Z");
		var jobEnd = parse("2022-01-01T12:30:00.000Z");
		var jobDeadline = parse("2022-01-01T13:30:00.000Z");

		// when
		var result = new ClientJobExecution(TEST_CLIENT, jobStart, jobEnd, jobDeadline, power, jobId);

		// then
		assertThat(result.getJobStatus()).isEqualTo(CREATED);
		assertThat(result.getJob()).matches(clientJob ->
				nonNull(clientJob.getJobInstanceId()) &&
						clientJob.getJobId().equals(jobId) &&
						clientJob.getClientIdentifier().equals(TEST_CLIENT.getName()) &&
						clientJob.getStartTime().equals(jobStart) &&
						clientJob.getEndTime().equals(jobEnd) &&
						clientJob.getDeadline().equals(jobDeadline) &&
						clientJob.getPower() == power);
		assertThat(result.getJobSimulatedDeadline()).isEqualTo(jobDeadline);
		assertThat(result.getJobSimulatedEnd()).isEqualTo(jobEnd);
		assertThat(result.getJobSimulatedStart()).isEqualTo(jobStart);
		assertThat(result.getJobDurationMap())
				.hasSize(JobClientStatusEnum.values().length)
				.allSatisfy((status, val) -> assertThat(val).isZero());
	}

	@Test
	@DisplayName("Test update job status duration map")
	void testUpdateJobStatusDuration() {
		// given
		var start = parse("2022-01-01T11:30:00.000Z");
		var end = parse("2022-01-01T11:31:00.000Z");

		var jobId = "1";
		var power = 20;
		var jobStart = parse("2022-01-01T11:30:00.000Z");
		var jobEnd = parse("2022-01-01T12:30:00.000Z");
		var jobDeadline = parse("2022-01-01T13:30:00.000Z");

		mockClientJobExecution = new ClientJobExecution(TEST_CLIENT, jobStart, jobEnd, jobDeadline, power, jobId);
		mockClientJobExecution.getTimer().startTimeMeasure(start);

		// when
		mockClientJobExecution.updateJobStatusDuration(IN_PROGRESS, end);

		// then
		assertThat(mockClientJobExecution.getJobStatus()).isEqualTo(IN_PROGRESS);
		assertThat(mockClientJobExecution.getTimer().getTimeStart().get()).isEqualTo(end);
		assertThat(mockClientJobExecution.getJobDurationMap()).containsEntry(CREATED, 60000L);
	}
}

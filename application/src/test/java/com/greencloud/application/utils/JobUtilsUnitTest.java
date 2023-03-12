package com.greencloud.application.utils;

import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDateAndServer;
import static com.greencloud.application.utils.JobUtils.getTimetableOfJobs;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.application.utils.TimeUtils.convertToRealTime;
import static com.greencloud.application.utils.TimeUtils.setSystemStartTime;
import static com.greencloud.application.utils.TimeUtils.useMockTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_TRANSFER;
import static java.time.Instant.now;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.greencloud.commons.domain.job.ImmutablePowerJob;
import com.greencloud.commons.domain.job.ImmutableServerJob;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.ServerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.AID;

class JobUtilsUnitTest {

	// TEST PARAMETERS

	private static Stream<Arguments> parametersGetById() {
		return Stream.of(Arguments.of("1", true), Arguments.of("10000", false));
	}

	private static Stream<Arguments> parametersGetByIdAndStart() {
		return Stream.of(
				Arguments.of(parse("2022-01-01T07:00:00.000Z"), "2", true),
				Arguments.of(parse("2022-01-01T04:30:00.000Z"), "1", false)
		);
	}

	private static Stream<Arguments> parametersGetByIdAndEnd() {
		return Stream.of(
				Arguments.of(parse("2022-01-01T10:00:00.000Z"), "1", true),
				Arguments.of(parse("2022-01-01T14:00:00.000Z"), "3", false)
		);
	}

	private static Stream<Arguments> parametersGetByIdAndStartInstant() {
		return Stream.of(
				arguments(ImmutableJobInstanceIdentifier.of("2", parse("2022-01-01T07:00:00.000Z")), true),
				Arguments.of(ImmutableJobInstanceIdentifier.of("1", parse("2022-01-01T06:00:00.000Z")), false));
	}

	private static Stream<Arguments> parametersGetByIdAndStartAndServer() {
		return Stream.of(
				arguments(ImmutableJobInstanceIdentifier.of("2", parse("2022-01-01T07:00:00.000Z")), 2, true),
				arguments(ImmutableJobInstanceIdentifier.of("2", parse("2022-01-01T07:00:00.000Z")), 1, false),
				arguments(ImmutableJobInstanceIdentifier.of("1", parse("2022-01-01T06:00:00.000Z")), 1, false));
	}

	private static Stream<Arguments> parametersIsJobUnique() {
		return Stream.of(Arguments.of("5", false), Arguments.of("3", true), Arguments.of("1", false));
	}

	private static Stream<Arguments> parametersIsJobStarted() {
		return Stream.of(arguments(IN_PROGRESS, true), arguments(ACCEPTED, false), arguments(ON_HOLD_TRANSFER, true));
	}

	private static Stream<Arguments> parametersGetSuccessRatio() {
		return Stream.of(
				Arguments.of(0, 0, -1D),
				Arguments.of(10, 5, 0.5));
	}

	// SETUP

	@BeforeEach
	void setUp() {
		useMockTime(parse("2022-01-01T09:00:00.000Z"), ZoneId.of("UTC"));
		setSystemStartTime(parse("2022-01-01T05:00:00.000Z"));
	}

	// TESTS

	@ParameterizedTest
	@MethodSource("parametersGetById")
	@DisplayName("Test getting job by id")
	void testGettingJobById(final String jobId, final boolean result) {
		final ClientJob mockJob1 = ImmutableClientJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10)
				.build();

		final ClientJob jobResult = JobUtils.getJobById(jobId, Map.of(mockJob1, IN_PROGRESS));
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetByIdAndStart")
	@DisplayName("Test getting power job by id and start time")
	void testGettingJobByIdAndStartTime(final Instant startTime, final String jobId, final boolean result) {
		final PowerJob mockJob1 = ImmutablePowerJob.builder().jobId("1")
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final PowerJob mockJob2 = ImmutablePowerJob.builder().jobId("2")
				.startTime(parse("2022-01-01T07:00:00.000Z"))
				.endTime(parse("2022-01-01T11:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(20).build();

		final PowerJob jobResult = JobUtils.getJobByIdAndStartDate(jobId, startTime,
				Map.of(mockJob1, CREATED, mockJob2, IN_PROGRESS));
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetByIdAndEnd")
	@DisplayName("Test getting power job by id and end time")
	void testGettingJobByIdAndEndTime(final Instant endTime, final String jobId, final boolean result) {
		final PowerJob mockJob1 = ImmutablePowerJob.builder().jobId("1")
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final PowerJob mockJob3 = ImmutablePowerJob.builder().jobId("3")
				.startTime(parse("2022-01-01T11:00:00.000Z"))
				.endTime(parse("2022-01-01T12:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(25).build();

		final PowerJob jobResult = JobUtils.getJobByIdAndEndDate(jobId, endTime,
				Map.of(mockJob1, CREATED, mockJob3, ACCEPTED));
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetByIdAndStartInstant")
	@DisplayName("Test getting power job by id and start time instant")
	void testGettingJobByIdAndStartTimeInstant(final JobInstanceIdentifier jobInstance, final boolean result) {
		final PowerJob mockJob1 = ImmutablePowerJob.builder().jobId("1")
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final PowerJob mockJob2 = ImmutablePowerJob.builder().jobId("2")
				.startTime(parse("2022-01-01T07:00:00.000Z"))
				.endTime(parse("2022-01-01T11:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(20).build();

		final PowerJob jobResult = JobUtils.getJobByIdAndStartDate(jobInstance,
				Map.of(mockJob1, CREATED, mockJob2, IN_PROGRESS));
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetByIdAndStartAndServer")
	@DisplayName("Test getting power job by id and start time instant")
	void testGettingJobByIdAndStartAndServer(final JobInstanceIdentifier jobInstance, final int serverIdx,
			final boolean result) {
		final AID mockServer1 = mock(AID.class);
		final AID mockServer2 = mock(AID.class);

		final ServerJob mockJob1 = ImmutableServerJob.builder()
				.jobId("1")
				.server(mockServer1)
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final ServerJob mockJob2 = ImmutableServerJob.builder()
				.jobId("2")
				.server(mockServer2)
				.startTime(parse("2022-01-01T07:00:00.000Z"))
				.endTime(parse("2022-01-01T11:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(20).build();

		final AID serverToUse = serverIdx == 1 ? mockServer1 : mockServer2;
		final PowerJob jobResult = getJobByIdAndStartDateAndServer(jobInstance, serverToUse,
				Map.of(mockJob1, CREATED, mockJob2, IN_PROGRESS));
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@Test
	@DisplayName("Test getting expected job end time for current time before")
	void testCalculateExpectedJobEndTime() {
		final PowerJob mockJob = ImmutablePowerJob.builder().jobId("6").power(30)
				.startTime(parse("2022-01-01T13:00:00.000Z"))
				.endTime(parse("2022-01-01T14:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.build();
		final Date expectedResult = Date.from(parse("2022-01-01T14:00:01.000Z"));

		final Date result = JobUtils.calculateExpectedJobEndTime(mockJob);

		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	@DisplayName("Test getting expected job end time for current time after")
	void testCalculateExpectedJobEndTimeCurrentTimeAfter() {
		useMockTime(parse("2022-01-01T19:00:00.000Z"), ZoneId.of("UTC"));

		final PowerJob mockJob = ImmutablePowerJob.builder().jobId("6").power(30)
				.startTime(parse("2022-01-01T13:00:00.000Z"))
				.endTime(parse("2022-01-01T14:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.build();
		final Date expectedResult = Date.from(parse("2022-01-01T19:00:01.000Z"));

		final Date result = JobUtils.calculateExpectedJobEndTime(mockJob);

		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	@DisplayName("Test getting current job instance not found")
	void testGettingCurrentJobInstanceNotFound() {
		useMockTime(parse("2022-01-01T19:00:00.000Z"), ZoneId.of("UTC"));
		final Map.Entry<PowerJob, JobExecutionStatusEnum> result = JobUtils.getCurrentJobInstance("1", setUpMockJobs());

		assertNull(result);
	}

	@Test
	@DisplayName("Test getting current job instance one instance")
	void testGettingCurrentJobInstanceOneInstance() {
		useMockTime(parse("2022-01-01T14:00:00.000Z"), ZoneId.of("UTC"));
		final Map.Entry<PowerJob, JobExecutionStatusEnum> result = JobUtils.getCurrentJobInstance("2", setUpMockJobs());

		assertNotNull(result);
		assertThat(result.getKey().getDeadline()).isEqualTo(parse("2022-01-01T20:00:00.000Z"));
		assertThat(result.getKey().getEndTime()).isEqualTo(parse("2022-01-01T15:00:00.000Z"));
	}

	@Test
	@DisplayName("Test getting current job instance two instances")
	void testGettingCurrentJobInstanceTwoInstances() {
		useMockTime(parse("2022-01-01T12:00:00.000Z"), ZoneId.of("UTC"));
		final PowerJob jobProcessing = ImmutablePowerJob.builder()
				.jobId("1")
				.startTime(parse("2022-01-01T10:30:00.000Z"))
				.endTime(parse("2022-01-01T13:30:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(20)
				.build();
		final Map<PowerJob, JobExecutionStatusEnum> testJobs = setUpMockJobs();
		testJobs.put(jobProcessing, IN_PROGRESS);

		final Map.Entry<PowerJob, JobExecutionStatusEnum> result = JobUtils.getCurrentJobInstance("1", testJobs);

		assertNotNull(result);
		assertThat(result.getKey().getPower()).isEqualTo(20);
		assertThat(result.getKey().getStartTime()).isEqualTo(parse("2022-01-01T10:30:00.000Z"));
	}

	@ParameterizedTest
	@MethodSource("parametersIsJobUnique")
	@DisplayName("Test is job unique by id")
	void testIsJobUnique(final String jobId, final boolean result) {
		final PowerJob mockJob = ImmutablePowerJob.builder()
				.jobId("1")
				.startTime(parse("2022-01-01T10:30:00.000Z"))
				.endTime(parse("2022-01-01T13:30:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10)
				.build();
		final Map<PowerJob, JobExecutionStatusEnum> testJobs = setUpMockJobs();
		testJobs.put(mockJob, IN_PROGRESS);

		assertThat(JobUtils.isJobUnique(jobId, testJobs)).isEqualTo(result);
	}

	@Test
	@DisplayName("Test is job started using map")
	void testIsJobStartedFromMap() {
		final PowerJob mockJob = ImmutablePowerJob.builder()
				.jobId("1")
				.startTime(parse("2022-01-01T10:30:00.000Z"))
				.endTime(parse("2022-01-01T13:30:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10)
				.build();
		final PowerJob mockJob2 = ImmutablePowerJob.builder()
				.jobId("2")
				.startTime(parse("2022-01-01T10:30:00.000Z"))
				.endTime(parse("2022-01-01T13:30:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10)
				.build();

		final Map<PowerJob, JobExecutionStatusEnum> testJobs = setUpMockJobs();
		testJobs.put(mockJob, IN_PROGRESS);
		testJobs.put(mockJob2, ACCEPTED);

		assertThat(isJobStarted(mockJob, testJobs)).isTrue();
		assertThat(isJobStarted(mockJob2, testJobs)).isFalse();
	}

	@ParameterizedTest
	@MethodSource("parametersIsJobStarted")
	@DisplayName("Test is job started using statuses")
	void testIsJobStarted(final JobExecutionStatusEnum status, final boolean result) {
		assertThat(isJobStarted(status)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetSuccessRatio")
	@DisplayName("Test getting job success ratio for component")
	void testGetJobSuccessRatio(long accepted, long failed, double result) {
		assertThat(JobUtils.getJobSuccessRatio(accepted, failed)).isEqualTo(result);
	}

	@Test
	@DisplayName("Test get timetables of jobs with repeatable time instances")
	void testGetTimetableOfJobsRepeatableInstances() {
		final ServerJob mockCandidatePowerJob = ImmutableServerJob.builder()
				.server(mock(AID.class))
				.jobId("6")
				.power(30)
				.startTime(parse("2022-01-01T13:00:00.000Z"))
				.endTime(parse("2022-01-01T14:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.build();

		setSystemStartTime(now());
		final List<Instant> result = getTimetableOfJobs(mockCandidatePowerJob, setUpMockJobsForTimeTables());

		assertThat(result).hasSize(8)
				.contains(convertToRealTime(parse("2022-01-01T13:00:00.000Z")))
				.contains(convertToRealTime(parse("2022-01-01T12:00:00.000Z")));
	}

	@Test
	@DisplayName("Test get timetables of jobs with job in processing")
	void testGetTimetableOfJobsJobInProcessing() {
		final ServerJob mockCandidatePowerJob = ImmutableServerJob.builder().jobId("6").power(30)
				.server(mock(AID.class))
				.startTime(parse("2022-01-01T13:00:00.000Z"))
				.endTime(parse("2022-01-01T14:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.build();
		final ServerJob jobProcessing = ImmutableServerJob.builder().jobId("10")
				.server(mock(AID.class))
				.startTime(parse("2022-01-01T10:30:00.000Z"))
				.endTime(parse("2022-01-01T13:30:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final Map<ServerJob, JobExecutionStatusEnum> testJobs = setUpMockJobsForTimeTables();
		testJobs.put(jobProcessing, JobExecutionStatusEnum.PROCESSING);

		setSystemStartTime(now());
		final List<Instant> result = getTimetableOfJobs(mockCandidatePowerJob, testJobs);

		assertThat(result).hasSize(8)
				.contains(convertToRealTime(parse("2022-01-01T13:00:00.000Z")))
				.contains(convertToRealTime(parse("2022-01-01T12:00:00.000Z")))
				.doesNotContain(convertToRealTime(parse("2022-01-01T13:30:00.000Z")));
	}

	// MOCK DATA

	/**
	 * Class creates mock jobs used in test scenarios.
	 * The following structure was used:
	 *
	 * ServerJob1 -> power: 10, time: 08:00 - 10:00, status: IN_PROGRESS,
	 * ServerJob2 -> power: 50,  time: 06:00 - 15:00, status: ON_HOLD
	 * ServerJob3 -> power: 25, time: 11:00 - 12:00, status: ACCEPTED
	 */
	private Map<ServerJob, JobExecutionStatusEnum> setUpMockJobsForTimeTables() {
		final ServerJob mockJob1 = ImmutableServerJob.builder().jobId("1")
				.server(mock(AID.class))
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final ServerJob mockJob2 = ImmutableServerJob.builder().jobId("2")
				.server(mock(AID.class))
				.startTime(parse("2022-01-01T06:00:00.000Z"))
				.endTime(parse("2022-01-01T15:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(50).build();
		final ServerJob mockJob3 = ImmutableServerJob.builder().jobId("3")
				.server(mock(AID.class))
				.startTime(parse("2022-01-01T11:00:00.000Z"))
				.endTime(parse("2022-01-01T12:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(25).build();
		final Map<ServerJob, JobExecutionStatusEnum> mockJobMap = new HashMap<>();
		mockJobMap.put(mockJob1, JobExecutionStatusEnum.IN_PROGRESS);
		mockJobMap.put(mockJob2, JobExecutionStatusEnum.ON_HOLD_PLANNED);
		mockJobMap.put(mockJob3, JobExecutionStatusEnum.ACCEPTED);
		return mockJobMap;
	}

	/**
	 * Class creates mock jobs used in test scenarios.
	 * The following structure was used:
	 *
	 * PowerJob1 -> power: 10, time: 08:00 - 10:00, status: IN_PROGRESS,
	 * PowerJob2 -> power: 50,  time: 06:00 - 15:00, status: ON_HOLD
	 * PowerJob3 -> power: 25, time: 11:00 - 12:00, status: ACCEPTED
	 */
	private Map<PowerJob, JobExecutionStatusEnum> setUpMockJobs() {
		final PowerJob mockJob1 = ImmutablePowerJob.builder().jobId("1")
				.startTime(parse("2022-01-01T08:00:00.000Z"))
				.endTime(parse("2022-01-01T10:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(10).build();
		final PowerJob mockJob2 = ImmutablePowerJob.builder().jobId("2")
				.startTime(parse("2022-01-01T06:00:00.000Z"))
				.endTime(parse("2022-01-01T15:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(50).build();
		final PowerJob mockJob3 = ImmutablePowerJob.builder().jobId("3")
				.startTime(parse("2022-01-01T11:00:00.000Z"))
				.endTime(parse("2022-01-01T12:00:00.000Z"))
				.deadline(parse("2022-01-01T20:00:00.000Z"))
				.power(25).build();
		final Map<PowerJob, JobExecutionStatusEnum> mockJobMap = new HashMap<>();
		mockJobMap.put(mockJob1, JobExecutionStatusEnum.IN_PROGRESS);
		mockJobMap.put(mockJob2, JobExecutionStatusEnum.ON_HOLD_PLANNED);
		mockJobMap.put(mockJob3, JobExecutionStatusEnum.ACCEPTED);
		return mockJobMap;
	}
}

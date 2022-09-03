package utils;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.AlgorithmUtils.getMaximumUsedPowerDuringTimeStamp;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import domain.job.ImmutableJob;
import domain.job.Job;

class AlgorithmUtilsTest {

	@Test
	@DisplayName("Test maximum capacity calculation for no jobs")
	void testMaximumCapacityEmptyJobs() {
		final Set<Job> jobList = Collections.EMPTY_SET;
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T10:30:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isZero();
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 1 Job")
	void testMaximumCapacityForOneJob() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T10:00:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(10)
				.build();
		final Set<Job> jobList = Set.of(mockJob1);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T10:30:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(10);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 2 Jobs overlapping in time interval")
	void testMaximumCapacityForTwoOverlappingJobs() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T10:00:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T10:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T12:00:00.111222400+00:00"))
				.power(15)
				.build();

		final Set<Job> jobList = Set.of(mockJob1, mockJob2);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(25);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 2 Jobs starting before time interval")
	void testMaximumCapacityForJobStartingBeforeTimeInterval() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T07:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T10:15:00.111222400+00:00"))
				.power(15)
				.build();

		final Set<Job> jobList = Set.of(mockJob1, mockJob2);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(25);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 2 Jobs finishing after time interval")
	void testMaximumCapacityForJobFinishingAfterTimeInterval() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T10:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T12:00:00.111222400+00:00"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T10:45:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T12:15:00.111222400+00:00"))
				.power(15)
				.build();

		final Set<Job> jobList = Set.of(mockJob1, mockJob2);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(25);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 2 Jobs overlapping time interval")
	void testMaximumCapacityForJobOverlappingTimeInterval() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T09:15:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(15)
				.build();

		final Set<Job> jobList = Set.of(mockJob1, mockJob2);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(25);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 3 Jobs with 1 outside interval")
	void testMaximumCapacityForJobOutsideInterval() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:10:00.111222400+00:00"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T10:15:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:30:00.111222400+00:00"))
				.power(20)
				.build();
		final Job mockJob3 = ImmutableJob.builder()
				.jobId("3")
				.clientIdentifier("Test Client 3")
				.startTime(OffsetDateTime.parse("2022-01-01T07:15:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00"))
				.power(15)
				.build();

		final Set<Job> jobList = Set.of(mockJob1, mockJob2, mockJob3);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(30);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for 2 non-overlapping Jobs")
	void testMaximumCapacityForNoOverlappingJobs() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T11:15:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T12:15:00.111222400+00:00"))
				.power(15)
				.build();

		final Set<Job> jobList = Set.of(mockJob1, mockJob2);
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T11:30:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(15);
	}

	@Test
	@DisplayName("Test maximum capacity calculation for complicated scenario")
	void testMaximumCapacityForComplicatedScenario() {
		final Set<Job> jobList = jobsForFirstComplicatedScenario();
		final OffsetDateTime startTime = OffsetDateTime.parse("2022-01-01T08:45:00.111222400+00:00");
		final OffsetDateTime endTime = OffsetDateTime.parse("2022-01-01T12:45:00.111222400+00:00");

		final int result = getMaximumUsedPowerDuringTimeStamp(jobList, startTime, endTime);

		assertThat(result).isEqualTo(15);
	}

	private Set<Job> jobsForFirstComplicatedScenario() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Test Client 1")
				.startTime(OffsetDateTime.parse("2022-01-01T08:30:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T08:45:00.111222400+00:00"))
				.power(1)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Test Client 2")
				.startTime(OffsetDateTime.parse("2022-01-01T08:00:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T09:00:00.111222400+00:00"))
				.power(2)
				.build();
		final Job mockJob3 = ImmutableJob.builder()
				.jobId("3")
				.clientIdentifier("Test Client 3")
				.startTime(OffsetDateTime.parse("2022-01-01T08:40:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:00:00.111222400+00:00"))
				.power(3)
				.build();
		final Job mockJob4 = ImmutableJob.builder()
				.jobId("4")
				.clientIdentifier("Test Client 4")
				.startTime(OffsetDateTime.parse("2022-01-01T10:00:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T12:00:00.111222400+00:00"))
				.power(4)
				.build();
		final Job mockJob5 = ImmutableJob.builder()
				.jobId("5")
				.clientIdentifier("Test Client 5")
				.startTime(OffsetDateTime.parse("2022-01-01T10:45:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T11:30:00.111222400+00:00"))
				.power(5)
				.build();
		final Job mockJob6 = ImmutableJob.builder()
				.jobId("6")
				.clientIdentifier("Test Client 6")
				.startTime(OffsetDateTime.parse("2022-01-01T11:15:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T12:30:00.111222400+00:00"))
				.power(6)
				.build();
		final Job mockJob7 = ImmutableJob.builder()
				.jobId("7")
				.clientIdentifier("Test Client 7")
				.startTime(OffsetDateTime.parse("2022-01-01T12:15:00.111222400+00:00"))
				.endTime(OffsetDateTime.parse("2022-01-01T13:00:00.111222400+00:00"))
				.power(7)
				.build();
		return Set.of(mockJob1, mockJob2, mockJob3, mockJob4, mockJob5, mockJob6, mockJob7);
	}
}

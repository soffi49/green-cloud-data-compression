package com.greencloud.application.agents.client.fixtures;

import static java.time.Instant.parse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.domain.ClientJobExecution;
import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.ImmutableJobParts;
import com.greencloud.application.domain.job.ImmutableJobStatusUpdate;
import com.greencloud.application.domain.job.ImmutableJobTimeFrames;
import com.greencloud.application.domain.job.JobParts;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.application.domain.job.JobTimeFrames;
import com.greencloud.commons.domain.job.ImmutableClientJob;

import jade.core.AID;

/**
 * Class contains common environment elements used in testing
 */
public class Fixtures {

	public final static AID TEST_CLIENT = new AID("test_client", AID.ISGUID);

	public static ClientJobExecution buildJobExecutionInstance(final String jobId) {
		var power = 20;
		var jobStart = parse("2022-01-01T11:30:00.000Z");
		var jobEnd = parse("2022-01-01T12:30:00.000Z");
		var jobDeadline = parse("2022-01-01T13:30:00.000Z");
		TEST_CLIENT.addAddresses("test_address");

		return new ClientJobExecution(TEST_CLIENT, jobStart, jobEnd, jobDeadline, power, jobId);
	}

	public static JobStatusUpdate buildJobStatusUpdate(final String jobId) {
		return ImmutableJobStatusUpdate.builder()
				.jobInstance(ImmutableJobInstanceIdentifier.builder()
						.jobId(jobId)
						.jobInstanceId("instance1")
						.startTime(parse("2022-01-01T10:00:00.000Z"))
						.build())
				.changeTime(parse("2022-01-01T09:00:00.000Z"))
				.build();
	}

	public static JobTimeFrames buildJobTimeFrames() {
		return ImmutableJobTimeFrames.builder()
				.jobId("1#part1")
				.newJobStart(parse("2022-01-01T11:40:00.000Z"))
				.newJobEnd(parse("2022-01-01T12:40:00.000Z"))
				.build();
	}

	public static JobParts buildJobParts() {
		return ImmutableJobParts.builder()
				.addJobParts(
						ImmutableClientJob.builder()
								.jobId("1#part1")
								.jobInstanceId("jobInstance1")
								.clientIdentifier("client1")
								.clientAddress("client_address")
								.startTime(parse("2022-01-01T11:30:00.000Z"))
								.endTime(parse("2022-01-01T12:30:00.000Z"))
								.deadline(parse("2022-01-01T13:30:00.000Z"))
								.power(10)
								.build(),
						ImmutableClientJob.builder()
								.jobId("1#part2")
								.jobInstanceId("jobInstance1")
								.clientIdentifier("client1")
								.clientAddress("client_address")
								.startTime(parse("2022-01-01T11:30:00.000Z"))
								.endTime(parse("2022-01-01T12:30:00.000Z"))
								.deadline(parse("2022-01-01T13:30:00.000Z"))
								.power(10)
								.build())
				.build();
	}

	public static ClientAgent setUpClient() {
		var clientJob = buildJobExecutionInstance("1");
		var clientJobParts = new ConcurrentHashMap<>(Map.of("1#part1", buildJobExecutionInstance("1#part1")));

		var mockClientAgent = spy(ClientAgent.class);

		doReturn(new AID("test_client", AID.ISGUID)).when(mockClientAgent).getAID();
		doReturn(clientJob).when(mockClientAgent).getJobExecution();
		doReturn(clientJobParts).when(mockClientAgent).getJobParts();

		return mockClientAgent;
	}

	public static void setUpClientMultipleJobParts(final ClientAgent clientAgent) {
		var clientJobParts = new ConcurrentHashMap<>(Map.of(
				"1#part1", buildJobExecutionInstance("1#part1"),
				"1#part2", buildJobExecutionInstance("1#part2")
		));

		doReturn(clientJobParts).when(clientAgent).getJobParts();
	}
}

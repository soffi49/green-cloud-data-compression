package com.greencloud.application.messages.fixtures;

import static jade.lang.acl.ACLMessage.REQUEST;
import static java.lang.String.format;
import static java.time.Instant.parse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.util.List;

import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.ImmutableJobParts;
import com.greencloud.application.domain.job.ImmutableJobPowerShortageTransfer;
import com.greencloud.application.domain.job.ImmutableJobStatusUpdate;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobParts;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.application.domain.weather.ImmutableMonitoringData;
import com.greencloud.application.domain.weather.ImmutableWeatherData;
import com.greencloud.application.domain.weather.MonitoringData;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.greencloud.commons.domain.location.ImmutableLocation;
import com.greencloud.commons.domain.location.Location;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Class contains common environment elements used in testing
 */
public class Fixtures {

	public final static AID TEST_CNA = new AID("test_cna", AID.ISGUID);
	public final static AID TEST_SERVER = new AID("test_server", AID.ISGUID);
	public final static AID TEST_SCHEDULER = new AID("test_scheduler", AID.ISGUID);
	public final static AID TEST_MONITORING = new AID("test_monitoring", AID.ISGUID);

	public static ClientJob buildClientJob() {
		return ImmutableClientJob.builder()
				.clientIdentifier("test_client")
				.jobId("1")
				.jobInstanceId("jobInstance1")
				.startTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T14:30:00.000Z"))
				.deadline(Instant.parse("2022-01-01T15:30:00.000Z"))
				.power(10)
				.build();
	}

	public static String buildClientJobContent() {
		return "{\"jobId\":\"1\","
				+ "\"jobInstanceId\":\"jobInstance1\","
				+ "\"startTime\":1641043800.000000000,"
				+ "\"endTime\":1641047400.000000000,"
				+ "\"deadline\":1641051000.000000000,"
				+ "\"power\":10,"
				+ "\"clientIdentifier\":\"test_client\"}";
	}

	public static ClientJob buildClientJobPart() {
		return ImmutableClientJob.builder()
				.clientIdentifier("test_client")
				.jobId("1#part1")
				.jobInstanceId("jobInstance1")
				.startTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T14:30:00.000Z"))
				.deadline(Instant.parse("2022-01-01T15:30:00.000Z"))
				.power(10)
				.build();
	}

	public static JobInstanceIdentifier buildJobInstance() {
		return ImmutableJobInstanceIdentifier.builder()
				.jobId("1")
				.jobInstanceId("jobInstance1")
				.startTime(parse("2022-01-01T13:30:00.000Z"))
				.build();
	}

	public static String buildJobInstanceContent() {
		return "{\"jobId\":\"1\","
				+ "\"jobInstanceId\":\"jobInstance1\","
				+ "\"startTime\":1641043800.000000000}";
	}

	public static JobStatusUpdate buildJobStatusUpdate() {
		return ImmutableJobStatusUpdate.builder()
				.jobInstance(buildJobInstance())
				.changeTime(parse("2022-01-01T13:30:00.000Z"))
				.build();
	}

	public static String buildJobStatusUpdateContent() {
		return "{\"jobInstance\":{"
				+ "\"jobId\":\"1\","
				+ "\"jobInstanceId\":\"jobInstance1\","
				+ "\"startTime\":1641043800.000000000},"
				+ "\"changeTime\":1641043800.000000000}";
	}

	public static JobParts buildJobParts() {
		var jobPart1 = ImmutableClientJob.builder()
				.jobId("1#part1")
				.jobInstanceId("jobInstance1")
				.startTime(parse("2022-01-01T13:30:00.000Z"))
				.endTime(parse("2022-01-01T14:30:00.000Z"))
				.deadline(parse("2022-01-01T15:30:00.000Z"))
				.power(10)
				.clientIdentifier("test_client")
				.build();
		var jobPart2 = ImmutableClientJob.builder()
				.jobId("1#part2")
				.jobInstanceId("jobInstance1")
				.startTime(parse("2022-01-01T13:30:00.000Z"))
				.endTime(parse("2022-01-01T14:30:00.000Z"))
				.deadline(parse("2022-01-01T15:30:00.000Z"))
				.power(10)
				.clientIdentifier("test_client")
				.build();

		return ImmutableJobParts.builder().jobParts(List.of(jobPart1, jobPart2)).build();
	}

	public static String buildJobPartsContent() {
		return "{\"jobParts\":["
				+ "{\"jobId\":\"1#part1\",\"jobInstanceId\":\"jobInstance1\",\"startTime\":1641043800.000000000,\"endTime\":1641047400.000000000,"
				+ "\"deadline\":1641051000.000000000,\"power\":10,\"clientIdentifier\":\"test_client\"}"
				+ ","
				+ "{\"jobId\":\"1#part2\",\"jobInstanceId\":\"jobInstance1\",\"startTime\":1641043800.000000000,\"endTime\":1641047400.000000000,"
				+ "\"deadline\":1641051000.000000000,\"power\":10,\"clientIdentifier\":\"test_client\"}"
				+ "]}";
	}

	public static String buildAdjustedJobContent() {
		return "{\"newJobStart\":1641043800.000000000,"
				+ "\"newJobEnd\":1641047400.000000000,"
				+ "\"jobId\":\"1\"}";
	}

	public static Location buildLocation() {
		return ImmutableLocation.builder().latitude(10.0).longitude(20.0).build();
	}

	public static String buildLocationContent() {
		return "{\"location\":{"
				+ "\"latitude\":10.0,"
				+ "\"longitude\":20.0"
				+ "},"
				+ "\"predictionError\":0.04}";
	}

	public static MonitoringData buildMonitoringData() {
		return ImmutableMonitoringData.builder()
				.addWeatherData(ImmutableWeatherData.builder()
						.time(parse("2022-01-01T13:30:00.000Z"))
						.windSpeed(10.0)
						.cloudCover(10.0)
						.temperature(12.0)
						.build())
				.build();
	}

	public static String buildMonitoringDataContent() {
		return "{\"weatherData\":[{"
				+ "\"time\":1641043800.000000000,"
				+ "\"temperature\":12.0,"
				+ "\"windSpeed\":10.0,"
				+ "\"cloudCover\":10.0"
				+ "}]}";
	}

	public static JobPowerShortageTransfer buildPowerShortageTransferJob() {
		return ImmutableJobPowerShortageTransfer.builder()
				.powerShortageStart(parse("2022-01-01T13:40:00.000Z"))
				.firstJobInstanceId(ImmutableJobInstanceIdentifier.copyOf(buildJobInstance()))
				.secondJobInstanceId(ImmutableJobInstanceIdentifier
						.copyOf(buildJobInstance())
						.withStartTime(parse("2022-01-01T13:40:00.000Z")))
				.originalJobInstanceId("jobInstance1")
				.build();
	}

	public static String buildPowerShortageTransferContent() {
		return "{\"originalJobInstanceId\":\"jobInstance1\","
				+ "\"firstJobInstanceId\":{"
				+ "\"jobId\":\"1\","
				+ "\"jobInstanceId\":\"jobInstance1\","
				+ "\"startTime\":1641043800.000000000"
				+ "},"
				+ "\"secondJobInstanceId\":{"
				+ "\"jobId\":\"1\","
				+ "\"jobInstanceId\":\"jobInstance1\","
				+ "\"startTime\":1641044400.000000000"
				+ "},"
				+ "\"powerShortageStart\":1641044400.000000000}";
	}

	public static ACLMessage buildRequest() {
		final ACLMessage testMessage = new ACLMessage(REQUEST);
		testMessage.setProtocol("test_protocol");
		testMessage.setConversationId("test_conversationId");
		testMessage.setSender(TEST_SERVER);

		return testMessage;
	}

	public static String buildJobWithProtocolContent(final String protocol) {
		return format("{\"jobInstanceIdentifier\":{"
				+ "\"jobId\":\"1\","
				+ "\"jobInstanceId\":\"jobInstance1\","
				+ "\"startTime\":1641043800.000000000"
				+ "},"
				+ "\"replyProtocol\":\"%s\"}", protocol);
	}
}

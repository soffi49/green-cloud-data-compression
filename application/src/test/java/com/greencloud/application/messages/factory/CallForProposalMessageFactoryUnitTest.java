package com.greencloud.application.messages.factory;

import static com.greencloud.application.messages.factory.CallForProposalMessageFactory.createCallForProposal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.commons.domain.job.ImmutablePowerJob;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

class CallForProposalMessageFactoryUnitTest {

	private static Stream<Arguments> parametersMessageParams() {
		final AID aid1 = new AID("Sender1", AID.ISGUID);
		final AID aid2 = new AID("Sender2", AID.ISGUID);
		final AID aid3 = new AID("Sender3", AID.ISGUID);

		return Stream.of(
				arguments(
						ImmutableJobInstanceIdentifier.builder()
								.jobId("1")
								.jobInstanceId("jobInstance1")
								.startTime(Instant.parse("2022-01-01T13:30:00.000Z"))
								.build(),
						List.of(aid1),
						"TEST_PROTOCOL1",
						"{\"jobId\":\"1\","
								+ "\"jobInstanceId\":\"jobInstance1\","
								+ "\"startTime\":1641043800.000000000}"),
				arguments(
						ImmutablePowerJob.builder()
								.jobId("1")
								.jobInstanceId("jobInstance1")
								.deadline(Instant.parse("2022-01-01T15:30:00.000Z"))
								.endTime(Instant.parse("2022-01-01T14:30:00.000Z"))
								.startTime(Instant.parse("2022-01-01T13:30:00.000Z")).power(20).build(),
						List.of(aid2, aid3),
						"TEST_PROTOCOL2",
						"{\"jobId\":\"1\","
								+ "\"jobInstanceId\":\"jobInstance1\","
								+ "\"startTime\":1641043800.000000000,"
								+ "\"endTime\":1641047400.000000000,"
								+ "\"deadline\":1641051000.000000000,"
								+ "\"power\":20}"));
	}

	@ParameterizedTest
	@MethodSource("parametersMessageParams")
	@DisplayName("Test creating call for proposal message")
	void testCreateCallForProposal(Object content, List<AID> receivers, String protocol, String expectedContent) {
		final ACLMessage result = createCallForProposal(content, receivers, protocol);
		final Iterable<AID> receiversIt = result::getAllReceiver;

		assertThat(result.getContent()).isEqualTo(expectedContent);
		assertThat(receiversIt)
				.anyMatch((val) -> receivers.stream().map(AID::getName).anyMatch((aid) -> aid.equals(val.getName())));
		assertThat(result.getProtocol()).isEqualTo(protocol);
	}
}

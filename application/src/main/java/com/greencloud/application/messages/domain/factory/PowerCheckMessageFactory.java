package com.greencloud.application.messages.domain.factory;

import static com.greencloud.application.utils.JobUtils.getTimetableOfJobs;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Objects.nonNull;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.ImmutableGreenSourceForecastData;
import com.greencloud.application.domain.ImmutableGreenSourceWeatherData;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.commons.job.ServerJob;
import com.greencloud.commons.message.MessageBuilder;

import jade.lang.acl.ACLMessage;

/**
 * Class storing methods used in creating messages for power checking
 */
public class PowerCheckMessageFactory {

	/**
	 * Method prepares the request message sent to monitoring agent to retrieve weather data
	 *
	 * @param greenEnergyAgent agent sending the request
	 * @param conversationId   identifier of the conversation
	 * @param protocol         message protocol
	 * @param job              (optional) server job
	 * @return request ACLMessage
	 */
	public static ACLMessage preparePowerCheckRequest(final GreenEnergyAgent greenEnergyAgent, final ServerJob job,
			final String conversationId, final String protocol) {
		return MessageBuilder.builder()
				.withPerformative(ACLMessage.REQUEST)
				.withObjectContent(createMessageContent(job, greenEnergyAgent))
				.withReceivers(greenEnergyAgent.getMonitoringAgent())
				.withMessageProtocol(protocol)
				.withConversationId(conversationId)
				.build();
	}

	/**
	 * Method prepares the response to the given weather request sent by the Server Agent
	 *
	 * @param monitoringData monitoring data
	 * @param request        original server request
	 * @return request ACLMessage
	 */
	public static ACLMessage prepareWeatherDataResponse(final MonitoringData monitoringData, final ACLMessage request) {
		final ACLMessage response = request.createReply();

		return MessageBuilder.builder()
				.copy(response)
				.withConversationId(request.getConversationId())
				.withPerformative(INFORM)
				.withObjectContent(monitoringData, error -> {
					error.printStackTrace();
					response.setPerformative(REFUSE);
				})
				.build();
	}

	private static Object createMessageContent(final ServerJob job, final GreenEnergyAgent greenEnergyAgent) {
		final boolean requestForJobInProcessing = nonNull(job);

		return requestForJobInProcessing ?
				ImmutableGreenSourceForecastData.builder()
						.location(greenEnergyAgent.getLocation())
						.timetable(getTimetableOfJobs(job, greenEnergyAgent.getServerJobs()))
						.build() :
				ImmutableGreenSourceWeatherData.builder()
						.location(greenEnergyAgent.getLocation())
						.predictionError(greenEnergyAgent.getWeatherPredictionError())
						.build();
	}
}

package utils;

import static agents.AbstractAgent.GUI_ENABLED;

import agents.AbstractAgent;
import agents.cloudnetwork.CloudNetworkAgent;
import jade.core.AID;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import agents.AbstractAgent;
import agents.cloudnetwork.CloudNetworkAgent;
import jade.core.AID;

/**
 * Class defines set of utilities used together with GUI Controller
 */
public class GUIUtils {

	/**
	 * Method updates the GUI to indicate that the job execution has finished
	 *
	 * @param agent agent updating the GUI
	 * @param jobId unique identifier of the GUI
	 */
	public static void announceFinishedJob(final CloudNetworkAgent agent, final String jobId) {
		if(!GUI_ENABLED) {
			return;
		}

		agent.getGuiController().updateActiveJobsCountByValue(-1);
		agent.getGuiController().updateAllJobsCountByValue(-1);
	}

	/**
	 * Method updates the GUI to indicate that a new job is planned to be executed
	 *
	 * @param agent agent updating the GUI
	 * @param jobId unique identifier of the GUI
	 */
	public static void announceBookedJob(final AbstractAgent agent, final String jobId) {
		if(!GUI_ENABLED) {
			return;
		}

		agent.getGuiController().updateAllJobsCountByValue(1);
	}

	/**
	 * Method updates the GUI to indicate that new client is using Cloud Network
	 *
	 * @param agent agent updating the GUI
	 */
	public static void announceNewClient(final AbstractAgent agent) {
		if(!GUI_ENABLED) {
			return;
		}

		agent.getGuiController().updateClientsCountByValue(1);
	}

	/**
	 * Method displays the message arrow in GUI
	 *
	 * @param agent    agent being the sender
	 * @param receiver address of the receiver
	 */
	public static void displayMessageArrow(final AbstractAgent agent, final AID receiver) {
		if(!GUI_ENABLED) {
			return;
		}

		agent.getGuiController()
				.displayMessageArrow(agent.getAgentNode(), Collections.singletonList(receiver.getLocalName()));
	}

	/**
	 * Method displays the message arrow in GUI
	 *
	 * @param agent     agent being the sender
	 * @param receivers addresses of the receivers
	 */
	public static void displayMessageArrow(final AbstractAgent agent, final List<AID> receivers) {
		if(!GUI_ENABLED) {
			return;
		}

		agent.getGuiController()
				.displayMessageArrow(agent.getAgentNode(), receivers.stream().map(AID::getLocalName).toList());
	}

	/**
	 * Method displays the message arrow in GUI
	 *
	 * @param agent     agent being the sender
	 * @param receivers iterator of addresses of the receivers
	 */
	public static void displayMessageArrow(final AbstractAgent agent, final Iterator<AID> receivers) {
		if(!GUI_ENABLED) {
			return;
		}

		final Iterable<AID> iterable = () -> receivers;
		final Stream<AID> stream = StreamSupport.stream(iterable.spliterator(), false);
		agent.getGuiController().displayMessageArrow(agent.getAgentNode(), stream.map(AID::getLocalName).toList());
	}
}

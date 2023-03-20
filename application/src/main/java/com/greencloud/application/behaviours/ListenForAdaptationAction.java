package com.greencloud.application.behaviours;

import static com.database.knowledge.domain.action.AdaptationActionsDefinitions.getActionParametersClass;
import static com.database.knowledge.domain.action.AdaptationActionsDefinitions.getAdaptationAction;
import static com.greencloud.application.utils.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareFailureReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareInformReply;
import static com.greencloud.commons.managingsystem.executor.ExecutorMessageProtocols.EXECUTE_ACTION_REQUEST;
import static java.util.Objects.nonNull;

import com.database.knowledge.domain.action.AdaptationAction;
import com.database.knowledge.domain.action.AdaptationActionEnum;
import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.commons.managingsystem.planner.AdaptationActionParameters;

import jade.core.behaviours.CyclicBehaviour;

/**
 * Generic behaviour that listens for adaptation requests
 */
public class ListenForAdaptationAction extends CyclicBehaviour {

	final AbstractAgent myAbstractAgent;

	public ListenForAdaptationAction(AbstractAgent myAbstractAgent) {
		this.myAbstractAgent = myAbstractAgent;
	}

	/**
	 * Method listens for adaptation requests and then handles it as specified for a given agent
	 */
	@Override
	public void action() {
		var message = myAbstractAgent.receive(EXECUTE_ACTION_REQUEST);

		if (nonNull(message)) {
			final AdaptationActionEnum adaptationActionEnum = AdaptationActionEnum.valueOf(message.getConversationId());
			final AdaptationAction adaptationAction = getAdaptationAction(adaptationActionEnum);
			final AdaptationActionParameters adaptationActionParameters =
					readMessageContent(message, getActionParametersClass(adaptationActionEnum));

			if (!adaptationActionParameters.dependsOnOtherAgents()) {
				if (myAbstractAgent.executeAction(adaptationAction, adaptationActionParameters)) {
					myAbstractAgent.send(prepareInformReply(message));
				} else {
					myAbstractAgent.send(prepareFailureReply(message));
				}
			} else {
				myAbstractAgent.executeAction(adaptationAction, adaptationActionParameters, message);
			}
		}
	}
}

package org.greencloud.managingsystem.service.executor;

import static com.database.knowledge.domain.action.AdaptationActionsDefinitions.getAdaptationAction;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.managingsystem.executor.ExecutorMessageProtocols.EXECUTE_ACTION_PROTOCOL;
import static com.greencloud.factory.constants.AgentControllerConstants.RUN_AGENT_DELAY;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.util.Optional.empty;
import static org.greencloud.managingsystem.agent.behaviour.executor.VerifyAdaptationActionResult.createForSystemAction;
import static org.greencloud.managingsystem.service.executor.logs.ManagingAgentExecutorLog.EXECUTING_ADAPTATION_ACTION_LOG;

import java.util.List;
import java.util.Map;

import org.greencloud.managingsystem.agent.AbstractManagingAgent;
import org.greencloud.managingsystem.agent.ManagingAgent;
import org.greencloud.managingsystem.agent.behaviour.executor.InitiateAdaptationActionRequest;
import org.greencloud.managingsystem.service.AbstractManagingService;
import org.greencloud.managingsystem.service.planner.plans.AbstractPlan;
import org.greencloud.managingsystem.service.planner.plans.SystemPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.database.knowledge.domain.action.AdaptationAction;
import com.database.knowledge.domain.goal.GoalEnum;
import com.google.common.annotations.VisibleForTesting;
import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.message.MessageBuilder;
import com.greencloud.factory.AgentControllerFactory;
import com.greencloud.factory.AgentControllerFactoryImpl;
import com.gui.agents.ManagingAgentNode;

import jade.core.Location;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;

/**
 * Service containing methods used in execution of the adaptation plan
 */
public class ExecutorService extends AbstractManagingService {

	private static final Logger logger = LoggerFactory.getLogger(ExecutorService.class);

	private AgentControllerFactory factory;

	public ExecutorService(AbstractManagingAgent managingAgent) {
		super(managingAgent);
	}

	@VisibleForTesting
	protected ExecutorService(ManagingAgent managingAgent, AgentControllerFactory factory) {
		super(managingAgent);
		this.factory = factory;
	}

	/**
	 * Executes adaptation action defined within the provided adaptation plan
	 *
	 * @param adaptationPlan plan containing all necessary data to correctly execute adaptation action
	 */
	public void executeAdaptationAction(final AbstractPlan adaptationPlan) {
		final AdaptationAction actionToBeExecuted = getAdaptationAction(adaptationPlan.getAdaptationActionEnum());
		final Map<GoalEnum, Double> initialGoalQualities = managingAgent.monitor().getCurrentGoalQualities();

		logger.info(EXECUTING_ADAPTATION_ACTION_LOG, actionToBeExecuted.getAction());

		if (adaptationPlan instanceof SystemPlan systemAdaptationPlan) {
			executeAdaptationActionOnSystem(systemAdaptationPlan, actionToBeExecuted, initialGoalQualities);
		} else {
			executeAdaptationActionOnAgent(adaptationPlan, initialGoalQualities);
		}

		adaptationPlan.disablePlanAction().run();
	}

	private void executeAdaptationActionOnAgent(final AbstractPlan adaptationPlan,
			final Map<GoalEnum, Double> initialGoalQualities) {
		final ACLMessage adaptationActionRequest = MessageBuilder.builder()
				.withPerformative(REQUEST)
				.withConversationId(adaptationPlan.getAdaptationActionEnum().toString())
				.withMessageProtocol(EXECUTE_ACTION_PROTOCOL)
				.withObjectContent(adaptationPlan.getActionParameters())
				.withReceivers(adaptationPlan.getTargetAgent())
				.build();

		managingAgent.addBehaviour(new InitiateAdaptationActionRequest(managingAgent, adaptationActionRequest,
				initialGoalQualities, adaptationPlan.getPostActionHandler(), adaptationPlan.enablePlanAction()));
	}

	private void executeAdaptationActionOnSystem(final SystemPlan systemAdaptationPlan,
			final AdaptationAction actionToBeExecuted, final Map<GoalEnum, Double> initialGoalQualities) {
		final List<AgentController> createdAgents = createAgents(systemAdaptationPlan);
		final Location location = systemAdaptationPlan.getSystemAdaptationActionParameters().getAgentsTargetLocation();

		factory.runAgentControllers(createdAgents, RUN_AGENT_DELAY);
		managingAgent.move().moveContainers(location, createdAgents);
		((ManagingAgentNode) managingAgent.getAgentNode()).logNewAdaptation(actionToBeExecuted.getAction(),
				getCurrentTime(), empty());
		managingAgent.addBehaviour(createForSystemAction(managingAgent,
				systemAdaptationPlan.getAdaptationActionEnum(), initialGoalQualities,
				systemAdaptationPlan.enablePlanAction()));
	}

	private List<AgentController> createAgents(SystemPlan systemAdaptationPlan) {
		final List<AgentArgs> args = systemAdaptationPlan.getSystemAdaptationActionParameters().getAgentsArguments();
		managingAgent.move().addAgentsToStructure(args);
		return args.stream()
				.map(agentArgs -> factory.createAgentController(agentArgs, managingAgent.getGreenCloudStructure()))
				.toList();
	}

	public void setFactory(AgentControllerFactory factory) {
		this.factory = factory;
	}
}

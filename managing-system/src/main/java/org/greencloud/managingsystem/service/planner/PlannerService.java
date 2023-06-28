package org.greencloud.managingsystem.service.planner;

import static com.database.knowledge.domain.action.AdaptationActionEnum.ADD_GREEN_SOURCE;
import static com.database.knowledge.domain.action.AdaptationActionEnum.ADD_SERVER;
import static com.database.knowledge.domain.action.AdaptationActionEnum.CHANGE_GREEN_SOURCE_WEIGHT;
import static com.database.knowledge.domain.action.AdaptationActionEnum.CONNECT_GREEN_SOURCE;
import static com.database.knowledge.domain.action.AdaptationActionEnum.DECREASE_GREEN_SOURCE_ERROR;
import static com.database.knowledge.domain.action.AdaptationActionEnum.DISABLE_SERVER;
import static com.database.knowledge.domain.action.AdaptationActionEnum.DISCONNECT_GREEN_SOURCE;
import static com.database.knowledge.domain.action.AdaptationActionEnum.ENABLE_SERVER;
import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_DEADLINE_PRIORITY;
import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_GREEN_SOURCE_ERROR;
import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_POWER_PRIORITY;
import static java.util.Objects.nonNull;
import static org.greencloud.managingsystem.service.planner.logs.ManagingAgentPlannerLog.CONSTRUCTING_PLAN_FOR_ACTION_LOG;
import static org.greencloud.managingsystem.service.planner.logs.ManagingAgentPlannerLog.COULD_NOT_CONSTRUCT_PLAN_LOG;
import static org.greencloud.managingsystem.service.planner.logs.ManagingAgentPlannerLog.NO_ACTIONS_LOG;
import static org.greencloud.managingsystem.service.planner.logs.ManagingAgentPlannerLog.SELECTING_BEST_ACTION_LOG;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.greencloud.managingsystem.agent.AbstractManagingAgent;
import org.greencloud.managingsystem.service.AbstractManagingService;
import org.greencloud.managingsystem.service.planner.plans.AbstractPlan;
import org.greencloud.managingsystem.service.planner.plans.AddGreenSourcePlan;
import org.greencloud.managingsystem.service.planner.plans.AddServerPlan;
import org.greencloud.managingsystem.service.planner.plans.ChangeGreenSourceWeightPlan;
import org.greencloud.managingsystem.service.planner.plans.ConnectGreenSourcePlan;
import org.greencloud.managingsystem.service.planner.plans.DecrementGreenSourceErrorPlan;
import org.greencloud.managingsystem.service.planner.plans.DisableServerPlan;
import org.greencloud.managingsystem.service.planner.plans.DisconnectGreenSourcePlan;
import org.greencloud.managingsystem.service.planner.plans.EnableServerPlan;
import org.greencloud.managingsystem.service.planner.plans.IncreaseDeadlinePriorityPlan;
import org.greencloud.managingsystem.service.planner.plans.IncreaseJobDivisionPowerPriorityPlan;
import org.greencloud.managingsystem.service.planner.plans.IncrementGreenSourceErrorPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.database.knowledge.domain.action.AdaptationAction;
import com.database.knowledge.domain.action.AdaptationActionEnum;
import com.database.knowledge.domain.goal.GoalEnum;
import com.google.common.annotations.VisibleForTesting;

/**
 * Service containing methods used in analyzing adaptation options and selecting adaptation plan
 */
public class PlannerService extends AbstractManagingService {

	private static final Logger logger = LoggerFactory.getLogger(PlannerService.class);

	private Map<AdaptationActionEnum, AbstractPlan> planForActionMap;

	public PlannerService(AbstractManagingAgent managingAgent) {
		super(managingAgent);
	}

	/**
	 * Method is used to trigger the system adaptation planning based on specific adaptation action qualities
	 *
	 * @param adaptationActions set of available adaptation actions with computed qualities
	 * @param violatedGoal      goal that has been violated
	 */
	public void trigger(final Map<AdaptationAction, Double> adaptationActions, final GoalEnum violatedGoal) {
		initializePlansForActions(violatedGoal);
		final Map<AdaptationAction, Double> executableActions = getPlansWhichCanBeExecuted(adaptationActions);

		if (executableActions.isEmpty()) {
			logger.info(NO_ACTIONS_LOG);
			return;
		}

		logger.info(SELECTING_BEST_ACTION_LOG);
		final AdaptationAction bestAction = selectBestAction(executableActions);
		final AbstractPlan selectedPlan = getPlanForAdaptationAction(bestAction);
		final boolean isPlanConstructed = nonNull(selectedPlan)
				&& nonNull(selectedPlan.constructAdaptationPlan());

		if (!isPlanConstructed) {
			logger.info(COULD_NOT_CONSTRUCT_PLAN_LOG);
			return;
		}

		logger.info(CONSTRUCTING_PLAN_FOR_ACTION_LOG, selectedPlan.getAdaptationActionEnum().getName());
		managingAgent.execute().executeAdaptationAction(selectedPlan);
	}

	@VisibleForTesting
	protected AdaptationAction selectBestAction(final Map<AdaptationAction, Double> adaptationActions) {
		final Predicate<AdaptationAction> actionFilter = action ->
				adaptationActions.keySet().stream().noneMatch(key -> key.getRuns() == 0) || action.getRuns() == 0;

		return adaptationActions.entrySet().stream()
				.filter(action -> actionFilter.test(action.getKey()))
				.max(Comparator.comparingDouble(Map.Entry::getValue))
				.orElse(adaptationActions.entrySet().stream().findFirst().orElseThrow())
				.getKey();
	}

	@VisibleForTesting
	protected Map<AdaptationAction, Double> getPlansWhichCanBeExecuted(
			final Map<AdaptationAction, Double> adaptationActions) {
		return adaptationActions.entrySet().stream()
				.filter(entry -> entry.getKey().getAvailable())
				.filter(entry -> planForActionMap.containsKey(entry.getKey().getAction()))
				.filter(entry -> planForActionMap.get(entry.getKey().getAction()).isPlanExecutable())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@VisibleForTesting
	protected AbstractPlan getPlanForAdaptationAction(final AdaptationAction action) {
		return planForActionMap.getOrDefault(action.getAction(), null);
	}

	protected void initializePlansForActions(final GoalEnum violatedGoal) {
		planForActionMap = new EnumMap<>(AdaptationActionEnum.class);

		planForActionMap.put(ADD_SERVER,
				new AddServerPlan(managingAgent, violatedGoal));
		planForActionMap.put(ADD_GREEN_SOURCE,
				new AddGreenSourcePlan(managingAgent, violatedGoal));
		planForActionMap.put(CONNECT_GREEN_SOURCE,
				new ConnectGreenSourcePlan(managingAgent, violatedGoal));
		planForActionMap.put(DISCONNECT_GREEN_SOURCE,
				new DisconnectGreenSourcePlan(managingAgent, violatedGoal));
		planForActionMap.put(INCREASE_DEADLINE_PRIORITY,
				new IncreaseDeadlinePriorityPlan(managingAgent, violatedGoal));
		planForActionMap.put(INCREASE_POWER_PRIORITY,
				new IncreaseJobDivisionPowerPriorityPlan(managingAgent, violatedGoal));
		planForActionMap.put(INCREASE_GREEN_SOURCE_ERROR,
				new IncrementGreenSourceErrorPlan(managingAgent, violatedGoal));
		planForActionMap.put(CHANGE_GREEN_SOURCE_WEIGHT,
				new ChangeGreenSourceWeightPlan(managingAgent, violatedGoal));
		planForActionMap.put(DECREASE_GREEN_SOURCE_ERROR,
				new DecrementGreenSourceErrorPlan(managingAgent, violatedGoal));
		planForActionMap.put(DISABLE_SERVER,
				new DisableServerPlan(managingAgent, violatedGoal));
		planForActionMap.put(ENABLE_SERVER,
				new EnableServerPlan(managingAgent, violatedGoal));
	}

	@VisibleForTesting
	protected void setPlanForActionMap(Map<AdaptationActionEnum, AbstractPlan> planForActionMap) {
		this.planForActionMap = planForActionMap;
	}
}

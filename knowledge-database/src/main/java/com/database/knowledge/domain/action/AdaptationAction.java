package com.database.knowledge.domain.action;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import com.database.knowledge.domain.goal.GoalEnum;

/**
 * Object describing adaptation action that can be executed by the Managing Agent over Green Cloud
 */
public class AdaptationAction {

	private final Integer actionId;
	private final AdaptationActionEnum action;
	private final GoalEnum goal;
	// describes delta for each adaptation goal value that given action caused
	private final Map<GoalEnum, ActionResult> actionResults;
	private final Boolean isAvailable;
	private final AdaptationActionTypeEnum type;
	private Integer runs;
	private Double executionDuration;

	public AdaptationAction(Integer actionId, AdaptationActionEnum action, AdaptationActionTypeEnum type,
			GoalEnum goal) {
		this.actionId = actionId;
		this.action = action;
		this.type = type;
		this.goal = goal;
		this.actionResults = stream(GoalEnum.values())
				.collect(toMap(goalEnum -> goalEnum, goalEnum -> new ActionResult(0.0D, 0)));
		this.isAvailable = true;
		this.runs = 0;
		this.executionDuration = 0.0;
	}

	public AdaptationAction(Integer actionId, AdaptationActionEnum action, AdaptationActionTypeEnum type,
			GoalEnum goal, Map<GoalEnum, ActionResult> actionResults, Boolean isAvailable, Integer runs,
			Double executionDuration) {
		this.actionId = actionId;
		this.action = action;
		this.type = type;
		this.goal = goal;
		this.isAvailable = isAvailable;
		this.actionResults = actionResults;
		this.runs = runs;
		this.executionDuration = executionDuration;
	}

	public Integer getActionId() {
		return actionId;
	}

	public AdaptationActionEnum getAction() {
		return action;
	}

	public GoalEnum getGoal() {
		return goal;
	}

	public Map<GoalEnum, ActionResult> getActionResults() {
		return actionResults;
	}

	/**
	 * Method returns the average differences in qualities associated with each of the actions
	 *
	 * @return map of actions along with corresponding quality differences
	 */
	public Map<GoalEnum, Double> getActionResultDifferences() {
		return actionResults.entrySet().stream()
				.collect(toMap(Map.Entry::getKey, result -> result.getValue().diff()));
	}

	/**
	 * Method updates the average time of action execution
	 *
	 * @param newExecutionDuration the latest action execution duration
	 */
	public void updateAvgExecutionDuration(long newExecutionDuration) {
		executionDuration =
				runs == 0 ? newExecutionDuration : (runs * executionDuration + newExecutionDuration) / (runs + 1);
	}

	/**
	 * Merges adaptation actions results already present in the database with new data provided by the managing agent.
	 * NOT TO BE CALLED EXPLICITLY, method is only used by the Timescale Database internally when saving the object
	 * into database.
	 *
	 * @param newActionResults new action results provided by Managing Agent when saved to database
	 */
	public void mergeActionResults(Map<GoalEnum, Double> newActionResults) {
		newActionResults.forEach((goalEnum, diff) -> {
			if (runs == 0) {
				actionResults.put(goalEnum, new ActionResult(diff, 1));
			} else {
				var actionResult = actionResults.get(goalEnum);
				actionResults.put(goalEnum,
						new ActionResult(getUpdatedGoalChange(actionResult, diff), actionResult.runs() + 1));
			}
		});
	}

	public Boolean getAvailable() {
		return isAvailable;
	}

	public Integer getRuns() {
		return runs;
	}

	public AdaptationActionTypeEnum getType() {
		return type;
	}

	/**
	 * Updates the number of how many times action was run.
	 * NOT TO BE CALLED EXPLICITLY, method is only used by the Timescale Database internally when saving the object
	 * into database
	 */
	public void increaseRuns() {
		this.runs += 1;
	}

	private double getUpdatedGoalChange(final ActionResult actionResult, final Double newDiff) {
		return (actionResult.diff() * actionResult.runs() + newDiff) / (actionResult.runs() + 1);
	}

	public double getExecutionDuration() {
		return executionDuration;
	}

	@Override
	public String toString() {
		return action.name();
	}
}

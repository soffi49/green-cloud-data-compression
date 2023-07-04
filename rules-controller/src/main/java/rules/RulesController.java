package rules;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

import jade.core.Agent;

public class RulesController {

	protected final RulesEngine rulesEngine;
	protected final Rules rules;
	protected Agent agent;

	public RulesController() {
		this.rulesEngine = new DefaultRulesEngine();
		this.rules = new Rules();
	}

	public void fire(final Facts facts)
	{
		rulesEngine.fire(rules, facts);
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
}

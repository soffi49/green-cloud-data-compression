package com.greencloud.application.agents.client;

import static com.greencloud.commons.agent.AgentType.CLIENT;

import java.util.HashMap;
import java.util.Map;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.application.agents.client.domain.ClientJobExecution;
import com.greencloud.application.agents.client.management.ClientStateManagement;

/**
 * Abstract agent class storing the data regarding Client Agent
 */
public abstract class AbstractClientAgent extends AbstractAgent {

	protected ClientStateManagement clientStateManagement;

	protected ClientJobExecution jobExecution;
	protected Map<String, ClientJobExecution> jobParts;
	protected boolean announced;
	protected boolean split;

	protected AbstractClientAgent() {
		super();
		jobParts = new HashMap<>();
		agentType = CLIENT;
	}

	public ClientStateManagement manage() {
		return clientStateManagement;
	}

	public Map<String, ClientJobExecution> getJobParts() {
		return jobParts;
	}

	public ClientJobExecution getJobExecution() {
		return jobExecution;
	}

	public void announce() {
		announced = true;
	}

	public boolean isAnnounced() {
		return announced;
	}

	public boolean isSplit() {
		return split;
	}

	public void split() {
		split = true;
	}

}

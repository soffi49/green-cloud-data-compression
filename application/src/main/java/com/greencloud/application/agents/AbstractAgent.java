package com.greencloud.application.agents;

import static com.greencloud.application.common.constant.LoggingConstant.MDC_AGENT_NAME;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_CLIENT_NAME;
import static com.greencloud.commons.agent.AgentType.CLIENT;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.database.knowledge.domain.action.AdaptationAction;
import com.database.knowledge.domain.agent.DataType;
import com.database.knowledge.domain.agent.MonitoringData;
import com.database.knowledge.timescale.TimescaleDatabase;
import com.greencloud.application.behaviours.ReceiveGUIController;
import com.greencloud.commons.agent.AgentType;
import com.greencloud.commons.managingsystem.planner.AdaptationActionParameters;
import com.gui.agents.AbstractAgentNode;
import com.gui.controller.GuiController;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Abstract class representing agent which has the connection with GUI controller
 */
public abstract class AbstractAgent extends Agent {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAgent.class);

	protected AgentType agentType;
	private GuiController guiController;
	private AbstractAgentNode agentNode;

	private ParallelBehaviour mainBehaviour;

	protected AbstractAgent() {
		setEnabledO2ACommunication(true, 2);
	}

	/**
	 * Abstract method used to validate if arguments of the given agent are correct
	 */
	protected void validateAgentArguments() {
	}

	/**
	 * Abstract method used to initialize given agent data
	 *
	 * @param arguments arguments passed by the user
	 */
	protected void initializeAgent(final Object[] arguments) {
	}

	/**
	 * Abstract method that is used to prepare starting behaviours for given agent
	 */
	protected List<Behaviour> prepareStartingBehaviours() {
		return emptyList();
	}

	/**
	 * Abstract method responsible for running starting behaviours
	 */
	protected void runStartingBehaviours() {
		addBehaviour(new ReceiveGUIController(this, prepareStartingBehaviours()));
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public AbstractAgentNode getAgentNode() {
		return agentNode;
	}

	public void setAgentNode(AbstractAgentNode agentNode) {
		this.agentNode = agentNode;
	}

	public GuiController getGuiController() {
		return guiController;
	}

	public void setGuiController(GuiController guiController) {
		this.guiController = guiController;
	}

	public void setMainBehaviour(ParallelBehaviour mainBehaviour) {
		this.mainBehaviour = mainBehaviour;
	}

	public void writeMonitoringData(DataType dataType, MonitoringData monitoringData) {
		agentNode.getDatabaseClient().writeMonitoringData(this.getAID().getName(), dataType, monitoringData);
	}

	public boolean executeAction(AdaptationAction adaptationAction, AdaptationActionParameters actionParameters) {
		// this method must be overwritten in agent types that will be a target to adaptation
		throw new UnsupportedOperationException();
	}

	public void executeAction(AdaptationAction adaptationAction, AdaptationActionParameters actionParameters,
			ACLMessage adaptationMessage) {
		// this method can be overwritten in agent types that will be a target to adaptation
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setup() {
		if (agentType.equals(CLIENT)) {
			MDC.put(MDC_CLIENT_NAME, super.getLocalName());
		} else {
			MDC.put(MDC_AGENT_NAME, super.getLocalName());
		}
		initializeAgent(getArguments());
		validateAgentArguments();
		runStartingBehaviours();
	}

	@Override
	protected void takeDown() {
		logger.info("I'm finished. Bye!");
		super.takeDown();
	}

	@Override
	public void clean(boolean ok) {
		if (!ok && Objects.nonNull(getGuiController()) && !agentType.equals(CLIENT)) {
			getGuiController().removeAgentNodeFromGraph(getAgentNode());
		}
		super.clean(ok);
	}

	@Override
	public void addBehaviour(Behaviour b) {
		if (Objects.nonNull(mainBehaviour)) {
			mainBehaviour.addSubBehaviour(b);
		} else {
			super.addBehaviour(b);
		}
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		guiController.addAgentNodeToGraph(agentNode);
		agentNode.setDatabaseClient(new TimescaleDatabase());
	}
}

package agents;

import com.gui.controller.GUIController;
import com.gui.agents.AbstractAgentNode;

import jade.core.Agent;

/**
 * Abstract class representing agent which has the connection with GUI controller
 */
public abstract class AbstractAgent extends Agent {
	private GUIController guiController;
	private AbstractAgentNode agentNode;

	public AbstractAgent() {
	}

	/**
	 * Class constructor
	 *
	 * @param guiController controller through which the agent can communicate with GUI
	 */
	public AbstractAgent(GUIController guiController) {
		this.guiController = guiController;
	}

	@Override
	protected void setup() {
		super.setup();
		setEnabledO2ACommunication(true, 2);
	}

	public AbstractAgentNode getAgentNode() {
		return agentNode;
	}

	public void setAgentNode(AbstractAgentNode agentNode) {
		this.agentNode = agentNode;
	}

	public GUIController getGuiController() {
		return guiController;
	}

	public void setGuiController(GUIController guiController) {
		this.guiController = guiController;
	}
}

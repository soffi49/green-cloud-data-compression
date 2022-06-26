package behaviours;

import agents.AbstractAgent;
import com.gui.controller.GUIController;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Behaviour responsible for retrieving the GUI controller for agent
 */
public class ReceiveGUIController extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveGUIController.class);

    private final AbstractAgent abstractAgent;
    private final List<Behaviour> initialBehaviours;

    /**
     * Behaviour constructor.
     *
     * @param agent             agent executing the behaviour
     * @param initialBehaviours initial behaviour for given agent
     */
    public ReceiveGUIController(final Agent agent, final List<Behaviour> initialBehaviours) {
        super(agent);
        this.abstractAgent = (AbstractAgent) agent;
        this.initialBehaviours = initialBehaviours;
    }

    /**
     * Method retrieves the GUI Controller and stores it in agent class
     */
    @Override
    public void action() {
        final Object controller = abstractAgent.getO2AObject();
        if (controller != null) {
            logger.info("[{}] Agent connected with the controller", myAgent.getName());
            abstractAgent.setGuiController((GUIController) controller);
            initialBehaviours.forEach(abstractAgent::addBehaviour);
        } else {
            block();
        }
    }
}

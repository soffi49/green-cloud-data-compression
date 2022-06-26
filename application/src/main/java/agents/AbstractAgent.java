package agents;

import behaviours.ReceiveGUIController;
import com.gui.controller.GUIController;
import jade.core.Agent;

/**
 * Abstract class representing agent which has the connection with GUI controller
 */
public abstract class AbstractAgent extends Agent {
    private GUIController guiController;

    public AbstractAgent() {
    }

    /**
     * Class constructor
     * @param guiController controller through which the agent can communicate with GUI
     */
    public AbstractAgent(GUIController guiController) {
        this.guiController = guiController;
    }

    @Override
    protected void setup() {
        super.setup();
        setEnabledO2ACommunication(true, 1);
    }

    public GUIController getGuiController() {
        return guiController;
    }
    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
    }
}

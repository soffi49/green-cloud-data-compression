package runner;

import static jade.core.Runtime.instance;

import com.gui.controller.GUIControllerImpl;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import runner.service.ScenarioService;

/**
 * Main method which runs the engine and the given scenario
 */
public class EngineRunner {
    public static void main(String[] args) {
        final Runtime runtime = instance();
        final Profile profile = new ProfileImpl();
        final GUIControllerImpl guiController = new GUIControllerImpl();

        profile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "6996");

        final ContainerController container = runtime.createMainContainer(profile);
        final ScenarioService scenarioService = new ScenarioService(container, guiController);

        guiController.createGUI();
        scenarioService.createAgentsFromScenarioFile("complicatedScenarioNoWeatherChanging");
    }
}

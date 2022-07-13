package runner;

import static jade.core.Runtime.instance;

import com.gui.controller.GUIControllerImpl;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import runner.service.ScenarioService;

/**
 * Main method which runs the engine and the given scenario
 */
public class EngineRunner {
    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final Runtime runtime = instance();
        final Profile profile = new ProfileImpl();
        final GUIControllerImpl guiController = new GUIControllerImpl();

        profile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "6996");

        final ContainerController container = runtime.createMainContainer(profile);
        //executorService.execute(() -> runRMAAgent(container));
        executorService.execute(guiController);
        executorService.execute(
            new ScenarioService(container, guiController, "complicatedScenarioNoWeatherChanging"));
    }

    /**
     * Method used to run Jade GUI
     *
     * @param container controller container
     */
    private static void runRMAAgent(final ContainerController container) {
        try {
            final AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
            rma.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

package runner;

import static jade.core.Runtime.instance;

import com.gui.AgentsGui;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.service.ScenarioService;

public class EngineRunner {
    public static void main(String[] args) {

        final Runtime runtime = instance();
        final Profile profile = new ProfileImpl();

        profile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT,"6996");

        final ContainerController container = runtime.createMainContainer(profile);
        final ScenarioService scenarioService = new ScenarioService(container);

        try {
            final AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
            rma.start();
            scenarioService.createAgentsFromScenarioFile("testAgents");
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

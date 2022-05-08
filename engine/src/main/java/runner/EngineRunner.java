package runner;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.utils.ScenarioService;

public class EngineRunner {
    public static void main(String[] args) {
        final jade.core.Runtime runtime = jade.core.Runtime.instance();
        final Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        final ContainerController container = runtime.createMainContainer(profile);
        try {
            final AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
            rma.start();
            ScenarioService.createAgentsFromScenarioFile("testAgents", container);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}

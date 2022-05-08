import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.ScenarioService;

public class EngineRunner {
    public static void main(String[] args) {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, "Test-Container");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        ContainerController container = runtime.createMainContainer(profile);
        try {
            ScenarioService.createAgentsFromScenarioFile("testAgents", container);
            AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
            rma.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}

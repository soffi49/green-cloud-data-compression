package runner.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gui.controller.GraphController;
import com.gui.domain.AgentNode;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.apache.commons.io.FileUtils;
import runner.domain.ImmutableGreenEnergyAgentArgs;
import runner.domain.ImmutableServerAgentArgs;
import runner.domain.ScenarioArgs;
import runner.domain.ServerAgentArgs;
import runner.factory.AgentControllerFactory;
import runner.factory.AgentControllerFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Service used in running the scenarios
 */
public class ScenarioService {

    private static final String RESOURCE_SCENARIO_PATH = "./scenarios/";
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    private final AgentControllerFactory factory;
    private final GraphController graphController;

    /**
     * Service constructor
     *
     * @param containerController container controller in which agents' controllers are to be created
     */
    public ScenarioService(ContainerController containerController, GraphController graphController) {
        this.factory = new AgentControllerFactoryImpl(containerController);
        this.graphController = graphController;
    }

    /**
     * Method creates the agent controllers that are to be run from the xml file
     *
     * @param fileName XML file containing the scenario description
     */
    public void createAgentsFromScenarioFile(final String fileName) {
        final File scenarioFile = getFileFromResourceFileName(fileName);
        try {
            final ScenarioArgs scenario = XML_MAPPER.readValue(scenarioFile, ScenarioArgs.class);

            if (Objects.nonNull(scenario.getServerAgentsArgs())) {

                scenario.getAgentsArgs().forEach(agentArgs -> {
                    try {
                        final AgentController agentController = factory.createAgentController(agentArgs);
                        graphController.addAgentNodeToGraph(factory.createAgentNode(agentArgs, scenario));
                        agentController.putO2AObject(graphController, AgentController.ASYNC);
                        agentController.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void adjustArguments(ScenarioArgs scenarioArgs) {
        scenarioArgs.getServerAgentsArgs().forEach(server -> {
            if(Objects.isNull(server.getLatitude()) || Objects.isNull(server.getLongitude())) {

            }
        });
    }

    private void computeServerLocation(ScenarioArgs scenarioArgs, ServerAgentArgs serverArgs) {
        final List<ImmutableGreenEnergyAgentArgs> ownedGreenSources =
                scenarioArgs.getGreenEnergyAgentsArgs().stream()
                        .filter(greenSource -> greenSource.getOwnerSever().equals(serverArgs.getName()))
                        .toList();
        final double longitude = ownedGreenSources.stream()
                .mapToDouble(args -> Double.parseDouble(args.getLongitude()))
                .sum() / ownedGreenSources.size();
        final double latitude = ownedGreenSources.stream()
                .mapToDouble(args -> Double.parseDouble(args.getLatitude()))
                .sum() / ownedGreenSources.size();
    }

    private File getFileFromResourceFileName(final String fileName) {
        try {
            final InputStream inputStream = ScenarioService.class.getClassLoader()
                    .getResourceAsStream(RESOURCE_SCENARIO_PATH + fileName + ".xml");
            final File tempFile;
            tempFile = File.createTempFile(fileName, ".xml");
            if (Objects.nonNull(inputStream)) {
                FileUtils.copyToFile(inputStream, tempFile);
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

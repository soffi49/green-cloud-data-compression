package runner.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gui.controller.GUIControllerImpl;
import com.gui.domain.nodes.AgentNode;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.apache.commons.io.FileUtils;
import runner.domain.ScenarioArgs;
import runner.factory.AgentControllerFactory;
import runner.factory.AgentControllerFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Service used in running the scenarios
 */
public class ScenarioService {

    private static final String RESOURCE_SCENARIO_PATH = "./scenarios/";
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    private final AgentControllerFactory factory;
    private final GUIControllerImpl guiController;

    /**
     * Service constructor
     *
     * @param containerController container controller in which agents' controllers are to be created
     */
    public ScenarioService(ContainerController containerController, GUIControllerImpl guiController) {
        this.factory = new AgentControllerFactoryImpl(containerController);
        this.guiController = guiController;
    }

    /**
     * Method creates the agent controllers that are to be run from the xml file
     *
     * @param fileName XML file containing the scenario description
     */
    public void createAgentsFromScenarioFile(final String fileName) {
        final File scenarioFile = getFileFromResourceFileName(fileName);
        final List<AgentController> agentsToRun = new ArrayList<>();
        try {
            final ScenarioArgs scenario = XML_MAPPER.readValue(scenarioFile, ScenarioArgs.class);

            if (Objects.nonNull(scenario.getServerAgentsArgs())) {

                scenario.getAgentsArgs().forEach(agentArgs -> {
                    try {
                        final AgentController agentController = factory.createAgentController(agentArgs);
                        final AgentNode agentNode = factory.createAgentNode(agentArgs, scenario);
                        guiController.addAgentNodeToGraph(agentNode);
                        agentController.putO2AObject(guiController, AgentController.ASYNC);
                        agentController.putO2AObject(agentNode, AgentController.ASYNC);
                        agentsToRun.add(agentController);
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                });
                guiController.createEdges();
                // next line is added on purpose! It waits for the graph to fully initialize
                //TimeUnit.SECONDS.sleep(7);
                for (AgentController agentController : agentsToRun) {
                    agentController.start();
                }
            }
        } catch (IOException | StaleProxyException e) {
            e.printStackTrace();
        }
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

package utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import domain.ScenarioArgs;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ScenarioService {

    private static final String RESOURCE_SCENARIO_PATH = "./scenarios/";

    public static void createAgentsFromScenarioFile(final String fileName, final ContainerController container) {
        final File scenarioFile = getFileFromResourceFileName(fileName);
        final XmlMapper mapper = new XmlMapper();
        try {
            final ScenarioArgs scenario = mapper.readValue(scenarioFile, ScenarioArgs.class);

            if (Objects.nonNull(scenario.getServerAgentsArgs())) {
                scenario.getServerAgentsArgs().forEach(serverAgent -> {
                    try {
                        final AgentController ag = container.createNewAgent(serverAgent.getName(),
                                                                            "agents.server.ServerAgent",
                                                                            new Object[]{serverAgent.getOwnerCloudNetwork(), serverAgent.getPrice(), serverAgent.getPower()});
                        ag.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                });
            }

            if (Objects.nonNull(scenario.getCloudNetworkAgentsArgs())) {
                scenario.getCloudNetworkAgentsArgs().forEach(cloudNetworkAgent -> {
                    try {
                        final AgentController ag = container.createNewAgent(cloudNetworkAgent.getName(),
                                                                            "agents.cloudnetwork.CloudNetworkAgent",
                                                                            new Object[]{});
                        ag.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                });
            }

            if (Objects.nonNull(scenario.getClientAgentsArgs())) {
                scenario.getClientAgentsArgs().forEach(clientAgent -> {
                    try {
                        final AgentController ag = container.createNewAgent(clientAgent.getName(),
                                                                            "agents.client.ClientAgent",
                                                                            new Object[]{clientAgent.getStartDate(), clientAgent.getEndDate(), clientAgent.getPower()});
                        ag.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getFileFromResourceFileName(final String fileName) {
        try {
            final InputStream inputStream = ScenarioService.class.getClassLoader().getResourceAsStream(RESOURCE_SCENARIO_PATH + fileName + ".xml");
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

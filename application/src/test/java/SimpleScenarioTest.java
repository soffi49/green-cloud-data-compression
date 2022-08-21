import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.Instant;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import agents.AbstractAgent;
import agents.client.ClientAgent;
import agents.cloudnetwork.CloudNetworkAgent;
import agents.greenenergy.GreenEnergyAgent;
import agents.monitoring.MonitoringAgent;
import agents.server.ServerAgent;
import common.TimeUtils;
import jade.junit.jupiter.JadeAgent;
import jade.junit.jupiter.JadeExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JadeExtension.class)
@MockitoSettings(strictness = LENIENT)
class SimpleScenarioTest {

	private static final Instant NOW = parse("2022-08-15T16:30:00.000Z");

	@JadeAgent(name = "monitor", type = "agents.monitoring.MonitoringAgent")
	private MonitoringAgent monitoringAgent;

	@JadeAgent(name = "windFarm", type = "agents.greenenergy.GreenEnergyAgent", arguments = { "monitor", "server",
			"200", "5", "52", "20", "WIND" })
	private GreenEnergyAgent greenEnergyAgent;

	@JadeAgent(name = "server", type = "agents.server.ServerAgent", arguments = {"cna", "10.5", "300"})
	private ServerAgent serverAgent;

	@JadeAgent(name = "cna", type = "agents.cloudnetwork.CloudNetworkAgent")
	private CloudNetworkAgent cloudNetworkAgent;

	@JadeAgent(name = "client", type = "agents.client.ClientAgent", arguments = { "15/08/2022 16:30",
			"15/08/2022 18:30", "100", "1" })
	private ClientAgent clientAgent;

	@BeforeAll
	static void init() {
		AbstractAgent.disableGui();
		TimeUtils.useMockTime(NOW);
	}

	@Test
	void simpleScenarioTest() throws InterruptedException {
		Thread.sleep(100000000);

		assertThat(monitoringAgent.getLocalName()).isEqualTo("monitor");
	}
}

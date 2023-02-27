package com.greencloud.application.agents.cloudnetwork.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;

import jade.core.AID;

class CloudNetworkConfigManagementUnitTest {

	// MOCK OBJECTS
	@Mock
	private Map<AID, Integer> MOCK_WEIGHTS_FOR_SERVERS_MAP;
	@Mock
	private CloudNetworkAgent mockCloudNetworkAgent;

	@BeforeEach
	void init() {
		var cloudNetworkMonitorManagement = new CloudNetworkConfigManagement(mockCloudNetworkAgent);
		mockCloudNetworkAgent = mock(CloudNetworkAgent.class);
		MOCK_WEIGHTS_FOR_SERVERS_MAP = initMap();

		cloudNetworkMonitorManagement.setWeightsForServersMap(MOCK_WEIGHTS_FOR_SERVERS_MAP);
		doReturn(cloudNetworkMonitorManagement).when(mockCloudNetworkAgent).manageConfig();
	}
	// TESTS

	@Test
	void testGetServerPercentages() {
		final Map<AID, Double> serverPercentages = mockCloudNetworkAgent.manageConfig().getPercentages();

		assertThat(serverPercentages)
				.as("Server percentage map has correct size")
				.hasSize(4)
				.as("Server percentage map contains correct fields")
				.containsExactlyInAnyOrderEntriesOf(Map.of(
						new AID("1", AID.ISGUID), 1.0 * 100 / 7,
						new AID("2", AID.ISGUID), 1.0 * 100 / 7,
						new AID("3", AID.ISGUID), 3.0 * 100 / 7,
						new AID("4", AID.ISGUID), 2.0 * 100 / 7
				));
	}

	private Map<AID, Integer> initMap() {
		Map<AID, Integer> map = new HashMap<>();
		map.put(new AID("1", AID.ISGUID), 1);
		map.put(new AID("2", AID.ISGUID), 1);
		map.put(new AID("3", AID.ISGUID), 3);
		map.put(new AID("4", AID.ISGUID), 2);
		return map;
	}
}

package org.greencloud.managingsystem.service.planner.plans;

import static com.database.knowledge.domain.agent.DataType.SERVER_MONITORING;
import static com.database.knowledge.domain.goal.GoalEnum.DISTRIBUTE_TRAFFIC_EVENLY;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.greencloud.managingsystem.agent.ManagingAgent;
import org.greencloud.managingsystem.service.monitoring.MonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.database.knowledge.domain.agent.AgentData;
import com.database.knowledge.domain.agent.server.ImmutableServerMonitoringData;
import com.database.knowledge.timescale.TimescaleDatabase;
import com.greencloud.application.utils.TimeUtils;
import com.greencloud.commons.args.agent.cloudnetwork.ImmutableCloudNetworkArgs;
import com.greencloud.commons.args.agent.server.ImmutableServerAgentArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;
import com.gui.agents.ManagingAgentNode;

class DisableServerPlanTest {

	@Mock
	ManagingAgent managingAgent;

	@Mock
	ManagingAgentNode managingAgentNode;

	@Mock
	private TimescaleDatabase timescaleDatabase;

	private DisableServerPlan disableServerPlan;

	@BeforeEach
	void setup() {
		var monitoringService = mock(MonitoringService.class);
		managingAgent = mock(ManagingAgent.class);
		timescaleDatabase = mock(TimescaleDatabase.class);
		managingAgentNode = mock(ManagingAgentNode.class);

		disableServerPlan = new DisableServerPlan(managingAgent, DISTRIBUTE_TRAFFIC_EVENLY);
		doReturn(timescaleDatabase).when(managingAgentNode).getDatabaseClient();
		doReturn(managingAgentNode).when(managingAgent).getAgentNode();
		doReturn(monitoringService).when(managingAgent).monitor();
		mockServerMonitoringData();
		mockNetworkStructure();
	}

	//TODO fix following test
	@Test
	@Disabled
	@DisplayName("Test if the plan is executable")
	void testIsExecutable() {
		boolean result = disableServerPlan.isPlanExecutable();

		assertThat(result).isTrue();
	}

	//TODO fix following test
	@Test
	@Disabled
	@DisplayName("Test getting the target agent")
	void testGetTargetAgent() {
		disableServerPlan.isPlanExecutable();
		var result = disableServerPlan.constructAdaptationPlan().getTargetAgent();

		assertThat(result.getName()).isEqualTo("server2");
	}

	private void mockServerMonitoringData() {
		var monitoringData1 = ImmutableServerMonitoringData.builder()
				.isDisabled(false)
				.currentMaximumCapacity(100)
				.currentTraffic(0)
				.availablePower(30D)
				.successRatio(0.0)
				.currentBackUpPowerUsage(0)
				.serverJobs(10)
				.build();
		var monitoringData2 = ImmutableServerMonitoringData.builder()
				.isDisabled(false)
				.currentMaximumCapacity(101)
				.currentTraffic(0)
				.availablePower(30D)
				.successRatio(0.0)
				.currentBackUpPowerUsage(0)
				.serverJobs(10)
				.build();
		var monitoringData3 = ImmutableServerMonitoringData.builder()
				.isDisabled(false)
				.currentMaximumCapacity(102)
				.currentTraffic(10.0)
				.availablePower(30D)
				.successRatio(0.0)
				.currentBackUpPowerUsage(0)
				.serverJobs(10)
				.build();
		var monitoringData4 = ImmutableServerMonitoringData.builder()
				.isDisabled(true)
				.currentMaximumCapacity(100)
				.currentTraffic(0)
				.availablePower(30D)
				.successRatio(0.0)
				.currentBackUpPowerUsage(0)
				.serverJobs(10)
				.build();
		var mockData = List.of(
				new AgentData(TimeUtils.getCurrentTime(), "server1", SERVER_MONITORING, monitoringData1),
				new AgentData(TimeUtils.getCurrentTime(), "server2", SERVER_MONITORING, monitoringData2),
				new AgentData(TimeUtils.getCurrentTime(), "server3", SERVER_MONITORING, monitoringData3),
				new AgentData(TimeUtils.getCurrentTime(), "server4", SERVER_MONITORING, monitoringData4)
		);
		doReturn(mockData).when(timescaleDatabase).readLastMonitoringDataForDataTypes(List.of(SERVER_MONITORING));
	}

	private void mockNetworkStructure() {
		var server1 = ImmutableServerAgentArgs.builder()
				.jobProcessingLimit("10")
				.latitude("50")
				.longitude("50")
				.ownerCloudNetwork("CNA1")
				.name("server1")
				.price("10")
				.maximumCapacity("100")
				.build();
		var server2 = ImmutableServerAgentArgs.builder()
				.jobProcessingLimit("10")
				.latitude("50")
				.longitude("50")
				.ownerCloudNetwork("CNA2")
				.name("server2")
				.price("10")
				.maximumCapacity("100")
				.build();
		var server3 = ImmutableServerAgentArgs.builder()
				.jobProcessingLimit("10")
				.latitude("50")
				.longitude("50")
				.ownerCloudNetwork("CNA2")
				.name("server3")
				.price("10")
				.maximumCapacity("100")
				.build();
		var server4 = ImmutableServerAgentArgs.builder()
				.jobProcessingLimit("10")
				.latitude("50")
				.longitude("50")
				.ownerCloudNetwork("CNA1")
				.name("server4")
				.price("10")
				.maximumCapacity("100")
				.build();
		var cna1 = ImmutableCloudNetworkArgs.builder()
				.name("CNA1")
				.build();
		var cna2 = ImmutableCloudNetworkArgs.builder()
				.name("CNA2")
				.build();
		var networkStructure = new ScenarioStructureArgs(
				null,
				null,
				List.of(cna1, cna2),
				List.of(server1, server2, server3, server4),
				emptyList(),
				emptyList()
		);

		doReturn(networkStructure).when(managingAgent).getGreenCloudStructure();
	}
}
